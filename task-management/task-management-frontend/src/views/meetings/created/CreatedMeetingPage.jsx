import { useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import {
  Box,
  Card,
  CardContent,
  Grid,
  IconButton,
  Tab,
  Tabs,
  Tooltip,
  Typography,
  Stepper,
  Step,
  StepLabel,
  Fade,
} from "@mui/material";
import { useSelector } from "react-redux";
import dayjs from "dayjs";
import MemberAssignmentsView from "./MemberAssignmentsView";
import MeetingMembersView from "./MeetingMembersView";
import MeetingSessionsView from "./MeetingSessionsView";
import { useDispatch } from "react-redux";

import { TaskStatus } from "../../../components/task/status";
import toast from "react-hot-toast";
import { getDiffDateWithCurrent } from "../../../utils/date.util";
import { Icon } from "@iconify/react";
import StatusActionButton from "../../../components/meetings/StatusActionButton";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import MeetingPlanDialog from "../../../components/meetings/MeetingPlanDialog";
import DescriptionText from "../../../components/common/text/DescriptionText";
import PropTypes from "prop-types";
import { updateMeetingPlan, updateStatus } from "../../../store/meeting-plan";

const steps = [
  { statusId: "PLAN_DRAFT", label: "Bản nháp" },
  { statusId: "PLAN_REG_OPEN", label: "Mở đăng ký" },
  { statusId: "PLAN_REG_CLOSED", label: "Đóng đăng ký" },
  { statusId: "PLAN_ASSIGNED", label: "Đã phân công" },
  { statusId: "PLAN_IN_PROGRESS", label: "Đang diễn ra" },
  { statusId: "PLAN_COMPLETED", label: "Hoàn thành" },
];

const statusMessages = {
  PLAN_REG_OPEN: "Đang chờ thành viên đăng ký tham gia.",
  PLAN_ASSIGNED: "Phân công đã hoàn tất, chuẩn bị bắt đầu họp.",
  PLAN_IN_PROGRESS: "Cuộc họp đang diễn ra.",
  PLAN_COMPLETED: "Cuộc họp đã hoàn thành.",
};

const statusTabMap = {
  PLAN_DRAFT: 0,
  PLAN_REG_OPEN: 0,
  PLAN_REG_CLOSED: 1,
  PLAN_ASSIGNED: 1,
  PLAN_IN_PROGRESS: 1,
  PLAN_COMPLETED: 1,
};

const CreatedMeetingPage = ({ scrollPositions, onScrollUpdate }) => {
  const dispatch = useDispatch();

  const { currentPlan, members, assignments } = useSelector(
    (state) => state.meetingPlan
  );
  const activeStep = steps.findIndex(
    (step) => step.statusId === currentPlan?.statusId
  );
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [isFinalizeDialogOpen, setIsFinalizeDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [tabValue, setTabValue] = useState(
    statusTabMap[currentPlan?.statusId] || 0
  );

  const scrollRef = useRef(null);

  useEffect(() => {
    setTabValue(statusTabMap[currentPlan?.statusId] || 0);
  }, [currentPlan?.statusId]);

  useLayoutEffect(() => {
    if (scrollRef.current) {
      const scrollTop = scrollPositions?.[tabValue] ?? 0;
      scrollRef.current.scrollTop = scrollTop;
    }
  }, [tabValue, scrollPositions]);

  const handleChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleStatusUpdate = async (newStatus) => {
    try {
      await dispatch(
        updateStatus({
          meetingPlanId: currentPlan.id,
          statusId: { statusId: newStatus },
        })
      ).unwrap();
      toast.success(`Cập nhật trạng thái thành công!`);
    } catch (err) {
      console.error(err);
      toast.error("Cập nhật trạng thái thất bại!");
    }
  };

  const handleCancel = () => {
    setIsConfirmDialogOpen(false);
    handleStatusUpdate("PLAN_CANCELED");
  };

  const handleFinalize = () => {
    setIsFinalizeDialogOpen(true);
  };

  const confirmFinalize = () => {
    setIsFinalizeDialogOpen(false);
    handleStatusUpdate("PLAN_ASSIGNED");
  };

  const initialValues = useMemo(
    () => ({
      name: currentPlan?.name,
      description: currentPlan?.description,
      registrationDeadline: dayjs(currentPlan.registrationDeadline).toDate(),
      location: currentPlan?.location,
      statusId: currentPlan?.statusId,
    }),
    [currentPlan]
  );

  const handleEdit = async (data) => {
    try {
      await dispatch(
        updateMeetingPlan({
          meetingPlanId: currentPlan.id,
          data: data,
        })
      ).unwrap();
      toast.success(`Cập nhật cuộc họp thành công!`);
    } catch (err) {
      console.error(err);
    }
  };

  const unassignedMembers = useMemo(() => {
    const unassignedAssignments = assignments.filter(
      (assignment) => !assignment.sessionId
    );
    return unassignedAssignments.map((assignment) => {
      const member = members.find((m) => m.id === assignment.userId);
      return (
        member || { id: assignment.userId, firstName: "Unknown", lastName: "" }
      );
    });
  }, [members, assignments]);

  const finalizeDialogContent = (
    <Box>
      <Typography variant="body1" >
        Sau khi hoàn tất phân công, danh sách phân công sẽ không thể chỉnh sửa.
        Bạn có chắc chắn muốn tiếp tục?
      </Typography>
      {unassignedMembers.length > 0 && (
        <Typography
          variant="body2"
          color="warning.dark"
          sx={{ mt: 2, fontWeight: 500 }}
        >
          Cảnh báo: {unassignedMembers.length} thành viên chưa được phân công
          vào bất kỳ phiên họp nào:
          {unassignedMembers
            .map((m) => ` ${m.firstName} ${m.lastName}`)
            .join(", ")}
          .
        </Typography>
      )}
    </Box>
  );

  return (
    <Box
      ref={scrollRef}
      onScroll={(e) => {
        onScrollUpdate(tabValue, e.target.scrollTop);
      }}
      sx={{ p: 3, overflowY: "auto", height: "85vh" }}
    >
      {/* Header */}
      <Box sx={{ mb: 5 }}>
        <Grid xs={12} md={12}>
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              ml: 3,
              pb: { md: 0, xs: 3 },
            }}
          >
            <Typography
              variant="h4"
              sx={{
                textTransform: "capitalize",
                wordBreak: "break-word",
                whiteSpace: "pre-wrap",
              }}
            >
              {currentPlan?.name || "-"}
            </Typography>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                flexWrap: "wrap",
                gap: 0.5,
              }}
            >
              <Typography variant="subtitle2" sx={{ fontSize: "0.875rem" }}>
                Được tạo cách đây
              </Typography>
              <Tooltip
                title={dayjs(currentPlan?.createdStamp).format(
                  "DD/MM/YYYY HH:mm:ss"
                )}
              >
                <Typography
                  sx={{
                    px: 1,
                    color: "primary.main",
                    fontWeight: 600,
                    cursor: "pointer",
                    fontSize: "0.875rem",
                  }}
                >{`${getDiffDateWithCurrent(currentPlan?.createdStamp).join(
                  " "
                )}`}</Typography>
              </Tooltip>
              <Typography variant="subtitle2" sx={{ fontSize: "0.875rem" }}>
                trước.
              </Typography>
            </Box>
          </Box>
        </Grid>
      </Box>

      {/* Details */}
      <Card
        sx={{
          mb: 3,
          pl: 2,
          borderRadius: 3,
          boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
        }}
      >
        <CardContent>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Typography variant="h6">Chi Tiết</Typography>
            <Box sx={{ display: "flex", gap: 2, mr: 5 }}>
              <Tooltip title="Chỉnh sửa">
                <IconButton
                  color="primary"
                  onClick={() => setIsEditDialogOpen(true)}
                  sx={{
                    color: "primary.main",
                    bgcolor: "primary.background",
                    transition: "0.3s",
                    "&:hover": {
                      backgroundColor: "primary.light",
                      color: "white",
                    },
                  }}
                >
                  <Icon icon="mingcute:pencil-line" fontSize={20} />
                </IconButton>
              </Tooltip>
              <Tooltip title="Hủy cuộc họp">
                <IconButton
                  color="error"
                  onClick={() => setIsConfirmDialogOpen(true)}
                  sx={{
                    color: "error.main",
                    bgcolor: "error.background",
                    transition: "0.3s",
                    "&:hover": {
                      backgroundColor: "error.light",
                      color: "white",
                    },
                  }}
                >
                  <Icon icon="tabler:cancel" fontSize={20} />
                </IconButton>
              </Tooltip>
            </Box>
          </Box>
          <Grid container spacing={3} alignItems="flex-start">
            <Grid item xs={12} md={8}>
              <DescriptionText text={currentPlan?.description} />
            </Grid>
            <Grid item xs={12} md={4}>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "flex-start",
                  gap: 3,
                  borderLeft: 1,
                  borderColor: "divider",
                  pl: 3,
                }}
              >
                <Tooltip title={"Trạng thái"}>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <Icon icon="f7:status" fontSize={16} />
                    <TaskStatus
                      status={currentPlan?.status}
                      sx={{
                        textTransform: "capitalize",
                        fontSize: "0.9rem",
                        fontWeight: 500,
                        py: 2,
                      }}
                    />
                  </Box>
                </Tooltip>
                <Tooltip title={"Hạn đăng ký"}>
                  <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <Icon fontSize={16} icon="mingcute:time-line" />
                    <Typography
                      variant="body1"
                      sx={{ fontSize: "0.9rem", fontWeight: 500, pl: 1 }}
                    >
                      {currentPlan?.registrationDeadline
                        ? dayjs(currentPlan.registrationDeadline).format(
                            "DD MMM YYYY, HH:mm A"
                          )
                        : "-"}
                    </Typography>
                  </Box>
                </Tooltip>
                <Tooltip title={"Địa điểm"}>
                  <Box
                    sx={{ display: "flex", alignItems: "flex-start", gap: 2 }}
                  >
                    <Box sx={{ flexShrink: 0, display: "block" }}>
                      <Icon fontSize={16} icon="mdi:location" />
                    </Box>
                    <Typography
                      variant="body1"
                      sx={{
                        fontSize: "0.9rem",
                        fontWeight: 500,
                        pl: 1,
                        wordBreak: "break-word",
                        overflowWrap: "break-word",
                      }}
                    >
                      {currentPlan?.location || "-"}
                    </Typography>
                  </Box>
                </Tooltip>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Progress Container */}
      <Card
        sx={{
          mb: 3,
          borderRadius: 3,
          boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
        }}
      >
        <CardContent sx={{ pb: 1 }}>
          <Typography variant="h5" sx={{ textAlign: "center", mb: 5 }}>
            Tiến trình cuộc họp
          </Typography>
          <Stepper
            activeStep={activeStep}
            alternativeLabel
            sx={{ maxWidth: "800px", mx: "auto", mb: 5 }}
          >
            {steps.map((step) => (
              <Step key={step.statusId}>
                <StepLabel>{step.label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              gap: 4,
              flexWrap: "wrap",
              mb: 4,
            }}
          >
            {currentPlan?.statusId === "PLAN_DRAFT" && (
              <StatusActionButton
                label="Mở đăng ký"
                icon="fa6-solid:paper-plane"
                instruction="Hãy thêm phiên họp và thành viên trước khi mở đăng ký!"
                tooltip="Bắt đầu giai đoạn đăng ký cho cuộc họp."
                onClick={() => handleStatusUpdate("PLAN_REG_OPEN")}
                color="warning.main"
              />
            )}
            {currentPlan?.statusId === "PLAN_REG_OPEN" && (
              <Typography
                variant="body1"
                color="text.secondary"
                sx={{ textAlign: "center" }}
              >
                {statusMessages["PLAN_REG_OPEN"]}
              </Typography>
            )}
            {currentPlan?.statusId === "PLAN_REG_CLOSED" && (
              <StatusActionButton
                label="Hoàn tất phân công"
                icon="fa6-solid:check-double"
                instruction="Hãy lưu danh sách phân công trước khi hoàn tất phân công!"
                tooltip="Xác nhận phân công nhiệm vụ hoàn tất."
                onClick={handleFinalize}
                color="success.light"
              />
            )}
            {currentPlan?.statusId === "PLAN_ASSIGNED" && (
              <Typography
                variant="body1"
                color="text.secondary"
                sx={{ textAlign: "center" }}
              >
                {statusMessages["PLAN_ASSIGNED"]}
              </Typography>
            )}
            {currentPlan?.statusId === "PLAN_IN_PROGRESS" && (
              <Typography
                variant="body1"
                color="text.secondary"
                sx={{ textAlign: "center" }}
              >
                {statusMessages["PLAN_IN_PROGRESS"]}
              </Typography>
            )}
            {currentPlan?.statusId === "PLAN_COMPLETED" && (
              <Typography
                variant="body1"
                color="text.secondary"
                sx={{ textAlign: "center" }}
              >
                {statusMessages["PLAN_COMPLETED"]}
              </Typography>
            )}
          </Box>
        </CardContent>
        <Tabs
          value={tabValue}
          onChange={handleChange}
          sx={{
            bgcolor: "white",
            borderTop: 1,
            borderColor: "divider",
            "& .MuiTab-root": {
              textTransform: "none",
              fontWeight: "medium",
              fontSize: "1rem",
              color: "text.primary",
            },
            "& .MuiTab-root.Mui-selected": {
              color: "success.dark",
              fontWeight: "bold",
            },
            "& .MuiTabs-indicator": {
              backgroundColor: "success.dark",
            },
          }}
        >
          <Tab label="Phiên họp & Thành viên" />
          <Tab label="Phân công phiên họp" />
        </Tabs>
      </Card>

      <Box>
        <Fade in={tabValue === 0} timeout={500}>
          <Box style={{ display: tabValue === 0 ? "block" : "none" }}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <MeetingSessionsView />
              </Grid>
              <Grid item xs={12} sm={6}>
                <MeetingMembersView />
              </Grid>
            </Grid>
          </Box>
        </Fade>

        <Fade in={tabValue === 1} timeout={500}>
          <Box style={{ display: tabValue === 1 ? "block" : "none" }}>
            <MemberAssignmentsView />
          </Box>
        </Fade>
      </Box>

      <ConfirmationDialog
        open={isConfirmDialogOpen}
        onClose={() => setIsConfirmDialogOpen(false)}
        onConfirm={handleCancel}
        title="Hủy cuộc họp"
        content={"Bạn có chắc chắn muốn hủy cuộc họp này không?"}
      />

      <ConfirmationDialog
        variant="warning"
        open={isFinalizeDialogOpen}
        onClose={() => setIsFinalizeDialogOpen(false)}
        onConfirm={confirmFinalize}
        title="Xác nhận hoàn tất phân công"
        content={finalizeDialogContent}
      />

      {isEditDialogOpen && (
        <MeetingPlanDialog
          initialValues={initialValues}
          onSubmit={handleEdit}
          openDialog={isEditDialogOpen}
          onClose={() => setIsEditDialogOpen(false)}
        />
      )}
    </Box>
  );
};

CreatedMeetingPage.propTypes = {
  onScrollUpdate: PropTypes.func,
  scrollPositions: PropTypes.object,
};

export default CreatedMeetingPage;
