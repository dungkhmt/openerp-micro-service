import { Grid } from "@mui/material";
import toast from "react-hot-toast";
import { useTaskContext } from "../../../../../hooks/useTaskContext";
import { TaskViewLeft } from "../../../../../views/task/TaskViewLeft";
import TaskViewRight from "../../../../../views/task/TaskViewRight";

const Task = () => {
  const { error } = useTaskContext();

  if (error) {
    toast.error("Có lỗi xảy ra khi tải dữ liệu");
    return null;
  }

  return (
    <Grid container spacing={5}>
      <Grid item lg={9} xs={12}>
        <TaskViewLeft />
      </Grid>
      <Grid item lg={3} xs={12}>
        <TaskViewRight />
      </Grid>
    </Grid>
  );
};

export default Task;
