import { useEffect, useState } from "react";
import { Typography, Card, CardContent, Box } from "@mui/material";
import { useSelector, useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { useParams } from "react-router";
import { Icon } from "@iconify/react";
import SessionRegisterDialog from "../../../components/meetings/SessionRegisterDialog";
import RegisterButton from "../../../components/meetings/RegisterButton";
import SessionCard from "../../../components/meetings/SessionCard";
import { updateMyMeetingSessions } from "../../../store/meeting-plan/meeting-sessions";
import {
  isRegistrationMode,
  isRegistrationOpen,
} from "../../../utils/meetingUtils";

const SessionRegistrationsView = () => {
  const dispatch = useDispatch();
  const { meetingId } = useParams();
  const { isCreator, currentPlan, myAssignment } = useSelector(
    (state) => state.meetingPlan
  );
  const { sessions, myRegistrations } = useSelector(
    (state) => state.meetingSessions
  );
  const [selectedSessions, setSelectedSessions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [openRegisterDialog, setOpenRegisterDialog] = useState(false);

  const dataList = isRegistrationMode(currentPlan.statusId)
    ? myRegistrations
    : myAssignment
    ? [myAssignment]
    : [];
  const hasData = dataList.length > 0;

  const cardColor = isRegistrationMode(currentPlan.statusId)
    ? hasData
      ? "primary"
      : "warning"
    : hasData
    ? "success"
    : "error";

  const allSelected =
    sessions.length > 0 && selectedSessions.length === sessions.length;

  useEffect(() => {
    if (openRegisterDialog) {
      const registeredSessionIds = myRegistrations.map((reg) => reg.id);
      setSelectedSessions(registeredSessionIds);
    } else {
      setSelectedSessions([]);
    }
  }, [openRegisterDialog, myRegistrations]);

  const handleToggle = (id) => {
    setSelectedSessions((prevSelected) =>
      prevSelected.includes(id)
        ? prevSelected.filter((sid) => sid !== id)
        : [...prevSelected, id]
    );
  };

  const handleToggleSelectAll = () => {
    if (allSelected) {
      setSelectedSessions([]);
    } else {
      setSelectedSessions(sessions.map((session) => session.id));
    }
  };

  const handleSave = async () => {
    try {
      setLoading(true);
      await dispatch(
        updateMyMeetingSessions({
          meetingPlanId: meetingId,
          data: { sessionIds: selectedSessions },
        })
      ).unwrap();
      toast.success("Đăng ký thành công!");
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
      setOpenRegisterDialog(false);
    }
  };

  return (
    <>
      {!isCreator && (
        <Card
          sx={{
            borderRadius: 3,
            border: (theme) => `2px solid ${theme.palette[cardColor].main}`,
            backgroundColor: (theme) => theme.palette[cardColor].background,
            mt: 3,
          }}
        >
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {isRegistrationMode(currentPlan.statusId)
                ? "Các Phiên Họp Đã Đăng Ký"
                : "Phiên Họp Được Phân Công"}
            </Typography>

            {hasData ? (
              <Box
                sx={{
                  maxHeight: "60vh",
                  overflowY: "auto",
                }}
              >
                {dataList.map((session) => (
                  <SessionCard
                    key={session.id}
                    session={session}
                    sx={{ bgcolor: "white" }}
                  />
                ))}
              </Box>
            ) : (
              <Box display="flex" alignItems="center" gap={1} sx={{ p: 2 }}>
                <Icon icon="mdi:alert-circle-outline" color="red" />
                <Typography
                  variant="body1"
                  color="textSecondary"
                  sx={{ fontStyle: "italic" }}
                >
                  {isRegistrationMode(currentPlan?.statusId)
                    ? "Bạn chưa đăng ký phiên họp nào"
                    : "Không có phiên họp nào được phân công."}
                </Typography>
              </Box>
            )}

            {isRegistrationMode(currentPlan?.statusId) && (
              <RegisterButton
                hasData={hasData}
                onClick={() => setOpenRegisterDialog(true)}
                disabled={
                  !isRegistrationOpen(
                    currentPlan?.statusId,
                    currentPlan?.registrationDeadline
                  )
                }
              />
            )}
          </CardContent>
        </Card>
      )}

      <SessionRegisterDialog
        open={openRegisterDialog}
        onClose={() => setOpenRegisterDialog(false)}
        selectedSessions={selectedSessions}
        sessions={sessions}
        onToggle={handleToggle}
        onToggleSelectAll={handleToggleSelectAll}
        allSelected={allSelected}
        onSave={handleSave}
        loading={loading}
      />
    </>
  );
};

export default SessionRegistrationsView;
