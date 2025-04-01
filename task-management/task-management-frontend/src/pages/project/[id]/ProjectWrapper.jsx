import dayjs from "dayjs";
import { useCallback, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Outlet, useParams } from "react-router-dom";
import {
  fetchMembers,
  fetchMyRole,
  fetchProject,
  resetProject,
  setLoading,
  clearErrors as clearProjectErrors,
} from "../../../store/project";
import { resetCalendarData } from "../../../store/project/calendar";
import { resetGanttData } from "../../../store/project/gantt-chart";
import { fetchStatisticData } from "../../../store/project/statistic";
import { resetTasksData } from "../../../store/project/tasks";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import { useAPIExceptionHandler } from "../../../hooks/useAPIExceptionHandler";
import {
  clearErrors as clearEventsErrors,
  fetchEvents,
  resetEvents,
} from "../../../store/project/events";

const ProjectWrapper = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { fetchLoading: projectLoading, errors: projectErrors } = useSelector(
    (state) => state.project
  );
  const { fetchLoading: eventsLoading, errors: eventsErrors } = useSelector(
    (state) => state.events
  );
  const { period } = useSelector((state) => state.statistic);

  const fetchProjectData = useCallback(async () => {
    // reset tasks data of previous project
    dispatch(resetTasksData());
    // reset gantt data
    dispatch(resetGanttData());
    dispatch(resetCalendarData());
    // reset project data
    dispatch(resetProject());
    //reset events data of previous project
    dispatch(resetEvents());
    // fetch project data
    await Promise.all([
      dispatch(fetchProject(id)),
      dispatch(fetchMembers(id)),
      dispatch(fetchMyRole(id)),
      dispatch(fetchEvents(id)),
      dispatch(
        fetchStatisticData({
          projectId: id,
          startDate: dayjs(period.startDate).unix(),
          endDate: dayjs(period.endDate).unix(),
        })
      ),
    ]);
    dispatch(setLoading(false));
  }, [dispatch, id]);

  useEffect(() => {
    fetchProjectData();
  }, [fetchProjectData]);

  useAPIExceptionHandler(projectLoading, projectErrors, clearProjectErrors);
  useAPIExceptionHandler(eventsLoading, eventsErrors, clearEventsErrors);

  if (projectLoading) return <CircularProgressLoading />;

  return <Outlet />;
};

export { ProjectWrapper };
