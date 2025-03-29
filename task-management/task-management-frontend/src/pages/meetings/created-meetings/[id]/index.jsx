import { useSelector } from "react-redux";
import { Helmet } from "react-helmet";
import { useNavigate, useParams } from "react-router";
import { useDispatch } from "react-redux";
import { useEffect, useRef } from "react";
import {
  fetchMeetingPlan,
  fetchMeetingPlanMembers,
  fetchMemberAssignments,
  setIsCreator,
  clearErrors as clearMeetingPlanErrors,
} from "../../../../store/meeting-plan";
import {
  fetchAllSessionRegistrations,
  fetchMeetingSessions,
  clearErrors as clearMeetingSessionErrors,
} from "../../../../store/meeting-plan/meeting-sessions";
import { CircularProgressLoading } from "../../../../components/common/loading/CircularProgressLoading";
import CreatedMeetingPage from "../../../../views/meetings/created/CreatedMeetingPage";
import { IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import { useAPIExceptionHandler } from "../../../../hooks/useAPIExceptionHandler";
import NotFound from "../../../../views/errors/NotFound";
import Unknown from "../../../../views/errors/Unknown";

const CreatedMeeting = () => {
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

  const scrollPositions = useRef({});

  if (pid && !scrollPositions.current[pid]) {
    scrollPositions.current[pid] = { 0: 0, 1: 0 };
  }

  const { errorType: planErrorType } = useAPIExceptionHandler(
    planLoading,
    planErrors,
    clearMeetingPlanErrors
  );
  const { errorType: sessionErrorType } = useAPIExceptionHandler(
    sessionLoading,
    sessionErrors,
    clearMeetingSessionErrors
  );

  useEffect(() => {
    if (!pid) return;
    const fetchData = async () => {
      try {
        dispatch(setIsCreator(true));
        if (!currentPlan || currentPlan.id !== pid) {
          await Promise.all([
            dispatch(fetchMeetingPlan(pid)),
            dispatch(fetchMeetingPlanMembers(pid)),
            dispatch(fetchMeetingSessions(pid)),
            dispatch(fetchMemberAssignments(pid)),
          ]);
        }
        await dispatch(fetchAllSessionRegistrations(pid));
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch, pid]);

  if (planErrorType === "notFound" || sessionErrorType === "notFound")
    return <NotFound />;
  if (planErrorType === "unknown" || sessionErrorType === "unknown")
    return <Unknown />;
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
      <CreatedMeetingPage
        scrollPositions={scrollPositions.current[pid]}
        onScrollUpdate={(tabValue, scrollTop) => {
          scrollPositions.current[pid][tabValue] = scrollTop;
        }}
      />
    </>
  );
};

export default CreatedMeeting;
