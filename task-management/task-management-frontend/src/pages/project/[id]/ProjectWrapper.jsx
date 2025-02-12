import { Box, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useCallback, useEffect } from "react";
import { Helmet } from "react-helmet";
import { useDispatch, useSelector } from "react-redux";
import { Outlet, useParams } from "react-router-dom";
import {
  fetchMembers,
  fetchProject,
  resetProject,
  setLoading,
} from "../../../store/project";
import { resetCalendarData } from "../../../store/project/calendar";
import { resetGanttData } from "../../../store/project/gantt-chart";
import { fetchStatisticData } from "../../../store/project/statistic";
import { resetTasksData } from "../../../store/project/tasks";
import NotFound from "../../../views/errors/NotFound";

const ProjectWrapper = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const { fetchLoading, errors } = useSelector((state) => state.project);
  const { period } = useSelector((state) => state.statistic);

  const fetchProjectData = useCallback(async () => {
    // reset tasks data of previous project
    dispatch(resetTasksData());
    // reset gantt data
    dispatch(resetGanttData());
    dispatch(resetCalendarData());
    // reset project data
    dispatch(resetProject());
    // fetch project data
    await Promise.all([
      dispatch(fetchProject(id)),
      dispatch(fetchMembers(id)),
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

  if (!fetchLoading && errors?.length > 0) {
    const is404Or403 =
      errors[0].message.includes("404") || errors[0].message.includes("403");
    if (is404Or403)
      return (
        <>
          <Helmet>
            <title>Project Not Found | Task management</title>
          </Helmet>
          <NotFound />
        </>
      );

    return (
      <>
        <Helmet>
          <title>Opps Occur Error | Task management</title>
        </Helmet>
        <Box
          sx={{
            mt: 6,
            display: "flex",
            alignItems: "center",
            flexDirection: "column",
          }}
        >
          <Typography variant="h4" color="error.dark">
            Có lỗi xảy ra
          </Typography>
          <Typography variant="body1" color="error.light">
            Có lẽ đã xảy ra lỗi khi tải dữ liệu, chúng tôi đang cố gắng để khắc
            phục. Vui lòng thử lại sau.
          </Typography>
        </Box>
      </>
    );
  }

  return <Outlet />;
};

export { ProjectWrapper };
