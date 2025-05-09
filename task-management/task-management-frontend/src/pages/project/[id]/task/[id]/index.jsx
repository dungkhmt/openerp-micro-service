import { Grid, Card, Box, Skeleton, Divider, IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { Helmet } from "react-helmet";
import { useTaskContext } from "../../../../../hooks/useTaskContext";
import { TaskViewLeft } from "../../../../../views/task/TaskViewLeft";
import TaskViewRight from "../../../../../views/task/TaskViewRight";
import { usePreventOverflow } from "../../../../../hooks/usePreventOverflow";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import NotFound from "../../../../../views/errors/NotFound";

const LeftLoading = () => (
  <Card sx={{ p: 6 }}>
    <Box sx={{ display: "flex", gap: 4, mb: 6 }}>
      <Skeleton variant="circular" width={80} height={80} />
      <Box>
        <Skeleton variant="text" width={350} />
        <Skeleton variant="text" width={160} />
      </Box>
    </Box>
    <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
    <Box sx={{ display: "flex", gap: 4, flexDirection: "column" }}>
      <Skeleton variant="text" width={50} />
      <Skeleton variant="text" width={200} />
      <Skeleton variant="text" width={70} />
      <Skeleton variant="rectangular" width={300} height={120} />
      <Skeleton variant="text" width={200} />
      <Skeleton variant="text" width={100} />
    </Box>
  </Card>
);

const RightLoading = () => (
  <Card sx={{ mr: 2 }}>
    <Box sx={{ p: 6 }}>
      <Skeleton variant="text" width={70} />
      <Skeleton variant="text" width={100} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={80} />
      <Skeleton variant="circular" width={35} height={35} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={70} />
      <Skeleton variant="text" width={120} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={80} />
      <Skeleton variant="text" width={120} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={70} />
      <Skeleton variant="text" width={40} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={60} />
      <Skeleton variant="text" width={35} />

      <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
      <Skeleton variant="text" width={65} />
      <Skeleton variant="text" width={45} />
    </Box>
  </Card>
);

const Task = () => {
  const navigate = useNavigate();
  const { task, error, isLoading: taskLoading } = useTaskContext();
  const { project, fetchLoading: projectLoading } = useSelector(
    (state) => state.project
  );
  const { ref, updateMaxHeight } = usePreventOverflow();

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (error) {
    if (error.response?.status === 404) return <NotFound />;
    else {
      toast.error("Có lỗi xảy ra khi tải dữ liệu");
      return null;
    }
  }

  const loading = taskLoading || projectLoading;

  return (
    <>
      <Helmet>
        <title>
          {`${
            task?.name?.length > 50
              ? `${task.name.slice(0, 50)}...`
              : task?.name ?? ""
          } | ${project?.name ?? ""} | Task management`}
        </title>
      </Helmet>
      <IconButton onClick={() => navigate(-1)}>
        <Icon fontSize={24} icon="mdi:arrow-left" />
      </IconButton>
      <Box
        ref={ref}
        sx={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          overflowY: "auto",
          mt: 1.5,
        }}
      >
        <Grid container spacing={5}>
          <Grid item lg={9} xs={12}>
            {loading ? <LeftLoading /> : <TaskViewLeft />}
          </Grid>
          <Grid item lg={3} xs={12}>
            {loading ? <RightLoading /> : <TaskViewRight />}
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

export default Task;
