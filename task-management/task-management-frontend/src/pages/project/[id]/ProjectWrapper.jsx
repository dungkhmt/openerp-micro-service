import { useEffect, useCallback } from "react";
import { useDispatch } from "react-redux";
import { Outlet, useParams } from "react-router-dom";
import { fetchMembers, fetchProject, setLoading } from "../../../store/project";
import { resetTasksData } from "../../../store/project/tasks";
import { resetGanttData } from "../../../store/project/gantt-chart";

const ProjectWrapper = () => {
  const { id } = useParams();
  const dispatch = useDispatch();

  const fetchProjectData = useCallback(async () => {
    dispatch(setLoading(true));
    // reset tasks data of previous project
    dispatch(resetTasksData());
    // reset gantt data
    dispatch(resetGanttData());
    // fetch project data
    await Promise.all([dispatch(fetchProject(id)), dispatch(fetchMembers(id))]);
    dispatch(setLoading(false));
  }, [dispatch, id]);

  useEffect(() => {
    fetchProjectData();
  }, [fetchProjectData]);

  return <Outlet />;
};

export { ProjectWrapper };
