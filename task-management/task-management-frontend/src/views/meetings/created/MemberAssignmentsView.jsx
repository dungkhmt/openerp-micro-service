import { useState, useEffect } from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  Select,
  MenuItem,
  Tooltip,
} from "@mui/material";
import { useSelector, useDispatch } from "react-redux";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import dayjs from "dayjs";
import { Icon } from "@iconify/react";
import toast from "react-hot-toast";
import { useParams } from "react-router";
import { MeetingPlanService } from "../../../services/api/meeting-plan.service";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import ExpandableSearch from "../../../components/mui/search/ExpandableSearch";
import { updateMemberAssignments } from "../../../store/meeting-plan";
import { removeDiacritics } from "../../../utils/stringUtils.js";

const MemberAssignmentsView = () => {
  const { meetingId } = useParams();
  const dispatch = useDispatch();
  const { memberRegistrations, sessions } = useSelector(
    (state) => state.meetingSessions
  );
  const { members, assignments, fetchLoading, currentPlan } = useSelector(
    (state) => state.meetingPlan
  );
  const [manualAssignments, setManualAssignments] = useState({});
  const [initialAssignments, setInitialAssignments] = useState({});
  const [loading, setLoading] = useState(false);
  const [expandedRows, setExpandedRows] = useState({});
  const [resetDialogOpen, setResetDialogOpen] = useState(false);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
  const [filteredMembers, setFilteredMembers] = useState(members);




  useEffect(() => {
    const initial = assignments.reduce((acc, { userId, sessionId }) => {
      acc[userId] = sessionId;
      return acc;
    }, {});
    setManualAssignments(initial);
    setInitialAssignments(initial);
    setHasUnsavedChanges(false);
  }, [assignments]);

  useEffect(() => {
    setFilteredMembers(members);
  }, [members]);

  const handleAssignChange = (memberId, newSessionId) => {
    setManualAssignments((prev) => {
      const updated = { ...prev, [memberId]: newSessionId };
      const isDifferent = Object.keys(updated).some(
        (key) =>
          updated[key] !== initialAssignments[key] ||
          !(key in initialAssignments)
      );
      setHasUnsavedChanges(isDifferent);
      return updated;
    });
  };

  const handleSaveAssignments = async () => {
    setLoading(true);
    const assignmentData = {
      assignments: Object.entries(manualAssignments).map(
        ([userId, sessionId]) => ({
          userId,
          sessionId,
        })
      ),
    };
    try {
      await dispatch(
        updateMemberAssignments({
          meetingPlanId: meetingId,
          data: assignmentData,
        })
      ).unwrap();
      setInitialAssignments(manualAssignments);
      toast.success("Lưu phân công thành công!");
      setHasUnsavedChanges(false);
    } catch (error) {
      console.error(error);
      toast.error("Lưu phân công thất bại!");
    } finally {
      setLoading(false);
    }
  };

  const handleAutoAssign = async () => {
    setLoading(true);
    const memberPreferences = members.reduce((acc, member) => {
      const userRegistrations = memberRegistrations.filter(
        (reg) => reg.userId === member.id
      );
      const preferredSessionIds = userRegistrations.map((reg) => reg.sessionId);
      if (preferredSessionIds.length > 0) acc[member.id] = preferredSessionIds;
      return acc;
    }, {});

    if (!Object.keys(memberPreferences).length) {
      toast.error("Không có dữ liệu ưu tiên");
      setLoading(false);
      return;
    }

    try {
      const res = await MeetingPlanService.autoAssignMembers(meetingId, {
        memberPreferences,
      });
      setManualAssignments(res.assignment);
      setHasUnsavedChanges(true);
      toast.success("Tự động phân công thành công!");
    } catch (error) {
      toast.error(error?.message || "Tự động phân công thất bại");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (searchTerm) => {
    if (!searchTerm) {
      setFilteredMembers(members);
    } else {
      const filtered = members.filter((member) => {
        const fullName = `${member.firstName ?? ""} ${member.lastName ?? ""}`
          .trim()
          .toLowerCase();
        const email = member.email?.toLowerCase() || "";
        return (
          removeDiacritics(fullName).includes(searchTerm.toLowerCase()) ||
          email.includes(searchTerm.toLowerCase())
        );
      });
      setFilteredMembers(filtered);
    }
  };

  const getSessionNameById = (sId) => {
    const session = sessions.find((s) => s.id === sId);
    if (!session) return "Unknown Session";
    const timeRange = `${dayjs(session.startTime).format("HH:mm")} - ${dayjs(
      session.endTime
    ).format("HH:mm")}`;
    return `${timeRange}`;
  };

  const getSessionTooltipById = (sId) => {
    const session = sessions.find((s) => s.id === sId);
    if (!session) return "Unknown Session";
    const date = dayjs(session.startTime).format("DD/MM/YYYY");
    const timeRange = `${dayjs(session.startTime).format("HH:mm")} - ${dayjs(
      session.endTime
    ).format("HH:mm")}`;
    return `${date} ${timeRange}`;
  };

  const toggleRowExpansion = (memberId) => {
    setExpandedRows((prev) => ({
      ...prev,
      [memberId]: !prev[memberId],
    }));
  };

  const hasAssignments = Object.keys(manualAssignments).length > 0;

  const handleResetClick = () => {
    if (hasAssignments) {
      setResetDialogOpen(true);
    } else {
      setManualAssignments(initialAssignments);
    }
  };

  const confirmReset = () => {
    setManualAssignments({ ...initialAssignments });
    setHasUnsavedChanges(false);
    setResetDialogOpen(false);
  };

  const closeDialogs = () => {
    setResetDialogOpen(false);
  };

  const isEditable = currentPlan?.statusId === "PLAN_REG_CLOSED";
  const MAX_INITIAL_SESSIONS = 8;

  return (
    <Card
      sx={{
        px: 7,
        py: 3,
        borderRadius: 3,
        boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
      >
        <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
          <Typography variant="h6">Phân công phiên họp</Typography>
          <Typography variant="body2" color="text.secondary">
            Vui lòng chờ đến khi đóng đăng ký để bắt đầu phân công!
          </Typography>
        </Box>

        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <ExpandableSearch
            placeholder="Tìm kiếm thành viên..."
            onSearchChange={handleSearch}
            iconSize={24}
            width={{ xs: "100%", sm: "200px", md: "300px" }}
          />
        </Box>
      </Box>
      <Card variant="outlined" sx={{ mb: 3 }}>
        <CardContent sx={{ p: 0, maxHeight: "70vh", overflow: "auto" }}>
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              minWidth: "600px",
            }}
          >
            <thead>
              <tr style={{ backgroundColor: "#f5f5f5" }}>
                <th
                  style={{
                    padding: "12px",
                    textAlign: "left",
                    borderBottom: "1px solid #ddd",
                    position: "sticky",
                    top: 0,
                    backgroundColor: "#f5f5f5",
                    zIndex: 1,
                  }}
                >
                  Thành viên
                </th>
                <th
                  style={{
                    padding: "12px",
                    textAlign: "left",
                    borderBottom: "1px solid #ddd",
                    position: "sticky",
                    top: 0,
                    backgroundColor: "#f5f5f5",
                    zIndex: 1,
                  }}
                >
                  Phiên họp đã chọn
                </th>
                <th
                  style={{
                    padding: "12px",
                    textAlign: "left",
                    borderBottom: "1px solid #ddd",
                    position: "sticky",
                    top: 0,
                    backgroundColor: "#f5f5f5",
                    zIndex: 1,
                  }}
                >
                  Được phân công
                </th>
              </tr>
            </thead>
            <tbody>
              {filteredMembers.map((member) => {
                const userRegistrations = memberRegistrations.filter(
                  (reg) => reg.userId === member.id
                );
                const registeredSessions = userRegistrations.map(
                  (reg) => reg.sessionId
                );
                const fullName =
                  `${member.firstName ?? ""} ${member.lastName ?? ""}`.trim() ||
                  "-";
                const email = member.email || "No email";
                const isExpanded = expandedRows[member.id];
                const visibleSessions = isExpanded
                  ? registeredSessions
                  : registeredSessions.slice(0, MAX_INITIAL_SESSIONS);

                return (
                  <tr
                    key={member.id}
                    style={{ borderBottom: "1px solid #eee" }}
                  >
                    <td style={{ padding: "12px", verticalAlign: "middle" }}>
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 1 }}
                      >
                        <UserAvatar user={member} />
                        <Box>
                          <Typography
                            sx={{
                              fontWeight: 600,
                              fontSize: "0.9rem",
                              color: "grey.A700",
                            }}
                          >
                            {fullName}
                          </Typography>
                          <Typography noWrap variant="caption">
                            {email || "No email provided"}
                          </Typography>
                        </Box>
                      </Box>
                    </td>
                    <td style={{ padding: "12px", verticalAlign: "middle" }}>
                      {registeredSessions.length ? (
                        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
                          {visibleSessions.map((sId) => (
                            <Tooltip
                              title={getSessionTooltipById(sId)}
                              key={sId}
                            >
                              <Box
                                sx={{
                                  display: "inline-block",
                                  borderRadius: 1,
                                  px: 1,
                                  py: 0.5,
                                  bgcolor: "grey.200",
                                  cursor: "pointer",
                                }}
                              >
                                <Typography
                                  color="text.primary"
                                  sx={{ fontSize: "0.8rem", fontWeight: 500 }}
                                >
                                  {getSessionNameById(sId)}
                                </Typography>
                              </Box>
                            </Tooltip>
                          ))}
                          {registeredSessions.length > MAX_INITIAL_SESSIONS && (
                            <Button
                              size="small"
                              variant="text"
                              onClick={() => toggleRowExpansion(member.id)}
                              sx={{ ml: 1, p: 0, minWidth: "auto" }}
                            >
                              {isExpanded
                                ? "Ẩn bớt"
                                : `+${
                                    registeredSessions.length -
                                    MAX_INITIAL_SESSIONS
                                  } nữa`}
                            </Button>
                          )}
                        </Box>
                      ) : (
                        <Typography variant="body2" color="text.secondary">
                          Không có ưu tiên
                        </Typography>
                      )}
                    </td>
                    <td style={{ padding: "12px", verticalAlign: "middle" }}>
                      <Select
                        size="small"
                        value={manualAssignments[member.id] || ""}
                        onChange={(e) =>
                          handleAssignChange(member.id, e.target.value)
                        }
                        disabled={loading || fetchLoading || !isEditable}
                        sx={{ minWidth: 150 }}
                      >
                        <MenuItem value="">
                          <em>Chọn phiên họp</em>
                        </MenuItem>
                        {registeredSessions.map((sId) => (
                          <MenuItem key={sId} value={sId}>
                            <Typography
                              variant="body2"
                              color="text.primary"
                              sx={{ fontWeight: 500 }}
                            >
                              {getSessionNameById(sId)}
                            </Typography>
                          </MenuItem>
                        ))}
                      </Select>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </CardContent>
      </Card>
      <Box sx={{ display: "flex", justifyContent: "flex-end", gap: 2 }}>
        <Button
          variant="text"
          onClick={handleResetClick}
          disabled={loading || !isEditable || !hasUnsavedChanges}
        >
          Đặt lại
        </Button>
        <Button
          variant="contained"
          onClick={handleSaveAssignments}
          disabled={loading || !isEditable || !hasUnsavedChanges}
          sx={{
            bgcolor: "primary.main",
            "&:hover": {
              bgcolor: "primary.dark",
            },
            transition: "background-color 0.3s",
          }}
        >
          Lưu
        </Button>
        <Button
          variant="contained"
          onClick={handleAutoAssign}
          startIcon={<Icon icon="mdi:auto-fix" />}
          disabled={loading || fetchLoading || !isEditable}
        >
          Phân công tự động
        </Button>
      </Box>

      {/* Reset Confirmation Dialog */}
      <ConfirmationDialog
        variant="warning"
        open={resetDialogOpen}
        onClose={closeDialogs}
        onConfirm={confirmReset}
        title="Xác nhận đặt lại"
        content="Bạn có chắc chắn muốn đặt lại các phân công? Các thay đổi chưa lưu
            sẽ được hoàn tác về trạng thái trước đó."
      />
    </Card>
  );
};

export default MemberAssignmentsView;
