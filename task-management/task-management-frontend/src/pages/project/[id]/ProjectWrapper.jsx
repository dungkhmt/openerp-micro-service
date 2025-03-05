import { Box, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useCallback, useEffect } from "react";
import { Helmet } from "react-helmet";
import { useDispatch, useSelector } from "react-redux";
import { Outlet, useParams } from "react-router-dom";
import {
  fetchMembers,
  fetchMyRole,
  fetchProject,
  resetProject,
  setLoading,
  clearErrors,
} from "../../../store/project";
import { resetCalendarData } from "../../../store/project/calendar";
import { resetGanttData } from "../../../store/project/gantt-chart";
import { fetchStatisticData } from "../../../store/project/statistic";
import { resetTasksData } from "../../../store/project/tasks";
import NotFound from "../../../views/errors/NotFound";
import { fetchEvents, resetEvents } from "../../../store/project/events";
import toast from "react-hot-toast";
import { useTranslation } from "react-i18next";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";

const ProjectWrapper = () => {
  const { id } = useParams();
  const { t } = useTranslation();
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

  if (fetchLoading) return <CircularProgressLoading />;

  if (!fetchLoading && errors?.length > 0) {
    const firstError = errors[0];

    if (firstError?.code?.includes("E03")) return <NotFound />;

    if (firstError?.code?.includes("E02")) {
      toast.error(t(firstError.message));
      dispatch(clearErrors());
      return <Outlet />;
    }

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
