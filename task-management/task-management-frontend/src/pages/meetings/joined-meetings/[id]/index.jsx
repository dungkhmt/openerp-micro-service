import { useSelector } from "react-redux";
import { Helmet } from "react-helmet";
import { useNavigate, useParams } from "react-router";
import { useDispatch } from "react-redux";
import { useEffect } from "react";
import {
  clearErrors as clearSessionErrors,
  fetchMeetingSessions,
  fetchMyMeetingSessions,
} from "../../../../store/meeting-plan/meeting-sessions";
import {
  clearErrors as clearPlanErrors,
  fetchMeetingPlan,
  fetchMeetingPlanMembers,
  fetchMyAssignment,
  setIsCreator,
} from "../../../../store/meeting-plan";
import { CircularProgressLoading } from "../../../../components/common/loading/CircularProgressLoading";
import JoinedMeetingPage from "../../../../views/meetings/joined/JoinedMeetingPage";
import { IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import { useAPIExceptionHandler } from "../../../../hooks/useAPIExceptionHandler";

const JoinedMeeting = () => {
  const navigate = useNavigate();
  const { pid } = useParams();
  const dispatch = useDispatch();
  const {
    currentPlan,
    fetchLoading: planLoading,
    errors: planErrors,
  } = useSelector((state) => state.meetingPlan);
  const { errors: sessionErrors, fetchLoading: sessionLoading } = useSelector(
    (state) => state.meetingSessions
  );

  useEffect(() => {
    if (!pid) return;
    const fetchData = async () => {
      try {
        dispatch(setIsCreator(false));
        await Promise.all([
          dispatch(fetchMeetingPlan(pid)),
          dispatch(fetchMeetingSessions(pid)),
        ]);
        if (!currentPlan || currentPlan.id !== pid) {
          await Promise.all([
            dispatch(fetchMyMeetingSessions(pid)),
            dispatch(fetchMyAssignment(pid)),
            dispatch(fetchMeetingPlanMembers(pid)),
          ]);
        }
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch, pid]);

  useAPIExceptionHandler(planLoading, planErrors, clearPlanErrors);
  useAPIExceptionHandler(sessionLoading, sessionErrors, clearSessionErrors);

  if (planLoading || sessionLoading || !currentPlan)
    return <CircularProgressLoading />;

  return (
    <>
      <Helmet>
        <title>
          {`${
            currentPlan?.name?.length > 30
              ? `${currentPlan.name.slice(0, 30)}...`
              : currentPlan?.name ?? ""
          } | Task management`}
        </title>
      </Helmet>
      <IconButton onClick={() => navigate(-1)} sx={{ mt: -2 }}>
        <Icon fontSize={24} icon="mdi:arrow-left" />
      </IconButton>
      <JoinedMeetingPage />
    </>
  );
};
export default JoinedMeeting;
