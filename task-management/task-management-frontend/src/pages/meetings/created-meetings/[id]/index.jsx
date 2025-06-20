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
  clearErrors as clearPlanErrors,
} from "../../../../store/meeting-plan";
import {
  fetchAllSessionRegistrations,
  fetchMeetingSessions,
  clearErrors as clearSessionErrors,
} from "../../../../store/meeting-plan/meeting-sessions";
import { CircularProgressLoading } from "../../../../components/common/loading/CircularProgressLoading";
import CreatedMeetingPage from "../../../../views/meetings/created/CreatedMeetingPage";
import { IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import { useAPIExceptionHandler } from "../../../../hooks/useAPIExceptionHandler";

const CreatedMeetingDetails = () => {
  const navigate = useNavigate();
  const { meetingId } = useParams();
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

  if (meetingId && !scrollPositions.current[meetingId]) {
    scrollPositions.current[meetingId] = 0;
  }

  useEffect(() => {
    if (!meetingId) return;
    const fetchData = async () => {
      try {
        dispatch(setIsCreator(true));
        if (!currentPlan || currentPlan.id !== meetingId) {
          await Promise.all([
            dispatch(fetchMeetingPlan(meetingId)),
            dispatch(fetchMeetingPlanMembers(meetingId)),
            dispatch(fetchMeetingSessions(meetingId)),
            dispatch(fetchMemberAssignments(meetingId)),
          ]);
        }
        await dispatch(fetchAllSessionRegistrations(meetingId));
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch, meetingId]);

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
      <CreatedMeetingPage
        scrollPositions={scrollPositions.current[meetingId]}
        onScrollUpdate={(tabValue, scrollTop) => {
          scrollPositions.current[meetingId] = scrollTop;
        }}
      />
    </>
  );
};

export default CreatedMeetingDetails;
