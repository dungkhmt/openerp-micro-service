import { useState } from "react";
import { Box, Typography, CardContent, Card, IconButton } from "@mui/material";
import { useDispatch, useSelector } from "react-redux";

import toast from "react-hot-toast";
import { useParams } from "react-router";
import { Icon } from "@iconify/react";
import CreateSessionsDialog from "../../../components/meetings/CreateSessionsDialog";
import SessionCard from "../../../components/meetings/SessionCard";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import dayjs from "dayjs";
import { createMeetingSessions, deleteMeetingSession } from "../../../store/meeting-plan/meeting-sessions";

const MeetingSessionsView = () => {
  const { pid } = useParams();
  const dispatch = useDispatch();
  const { isCreator, currentPlan } = useSelector((state) => state.meetingPlan);
  const { sessions } = useSelector((state) => state.meetingSessions);
  const [openSessionDialog, setOpenSessionDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deleteSession, setDeleteSession] = useState(null);

  const isEditable =
    isCreator &&
    (currentPlan?.statusId === "PLAN_DRAFT" ||
      currentPlan?.statusId === "PLAN_REG_OPEN");

  const onClose = () => {
    setOpenSessionDialog(false);
  };

  const handleDeleteClick = (session) => {
    setDeleteSession(session);
    setOpenDeleteDialog(true);
  };

  const handleDeleteDialogClose = () => {
    setOpenDeleteDialog(false);
    setDeleteSession(null);
  };

  const handleDelete = async () => {
    if (!deleteSession) return;
    try {
      await dispatch(
        deleteMeetingSession({
          meetingPlanId: pid,
          meetingSessionId: deleteSession.id,
        })
      ).unwrap();
      toast.success("Xóa phiên họp thành công!");
    } catch (err) {
      console.error(err);
    } finally {
      setOpenDeleteDialog(false);
      setDeleteSession(null);
    }
  };

  const handleSave = async (sessionData) => {
    const { sessions } = sessionData;

    const batchRequest = {
      sessions: sessions.map((session) => ({
        startTime: session.start,
        endTime: session.end,
      })),
    };

    try {
      await dispatch(
        createMeetingSessions({ meetingPlanId: pid, data: batchRequest })
      ).unwrap();
      toast.success("Tạo phiên họp thành công!");
    } catch (error) {
      console.error(error);
    } finally {
      onClose();
    }
  };

  return (
    <>
      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          {/* Header: Title + Add Button */}
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Typography variant="h6">Phiên họp</Typography>
              <Typography
                variant="body2"
                sx={{ fontWeight: 600, color: "text.secondary" }}
              >
                ({sessions?.length})
              </Typography>
            </Box>

            {isEditable && (
              <IconButton
                onClick={() => setOpenSessionDialog(true)}
                sx={{
                  bgcolor: "grey.100",
                  "&:hover": { bgcolor: "grey.300" },
                }}
              >
                <Icon icon="stash:plus-solid" fontSize={20} />
              </IconButton>
            )}
          </Box>

          <Box sx={{ overflowY: "auto", maxHeight: "60vh" }}>
            {sessions.length > 0 ? (
              sessions.map((session) => (
                <SessionCard
                  key={session.id}
                  session={session}
                  handleDeleteClick={handleDeleteClick}
                  isEditable={isEditable}
                />
              ))
            ) : (
              <Typography
                variant="body2"
                color="textSecondary"
                sx={{ fontStyle: "italic" }}
              >
                Không có phiên họp.
              </Typography>
            )}
          </Box>
        </CardContent>
      </Card>

      {/* Hidden anchor for popper positioning */}
      <Box
        id="datetimepicker"
        style={{ position: "fixed", top: 0, right: 0, width: 0, height: 0 }}
      />

      {/* The PlanSessionsDialog component */}
      <CreateSessionsDialog
        open={openSessionDialog}
        onClose={onClose}
        onCreate={handleSave}
      />

      {/* Delete Confirmation Dialog */}
      <ConfirmationDialog
        open={openDeleteDialog}
        onClose={handleDeleteDialogClose}
        onConfirm={handleDelete}
        title="Xác nhận xóa phiên họp"
        content={
          <>
            Bạn có chắc chắn muốn xóa phiên họp từ{" "}
            <strong>{dayjs(deleteSession?.startTime).format("HH:mm A")}</strong>{" "}
            đến{" "}
            <strong>{dayjs(deleteSession?.endTime).format("HH:mm A")}</strong>{" "}
            ngày{" "}
            <strong>
              {dayjs(deleteSession?.startTime).format("DD MMM YYYY")}
            </strong>{" "}
            không? Hành động này không thể hoàn tác.
          </>
        }
        confirmText="Xóa"
        cancelText="Hủy"
        variant="error"
      />
    </>
  );
};

export default MeetingSessionsView;
