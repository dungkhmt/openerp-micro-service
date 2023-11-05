import LocalFireDepartmentIcon from "@mui/icons-material/LocalFireDepartment";
import { Box, Grid, Typography } from "@mui/material";
import PropTypes from "prop-types";
import { Link } from "react-router-dom";
import CategoryElement from "../common/CategoryElement";
import StatusElement from "../common/StatusElement";
import { boxChildComponent } from "../utils/constant";

const AssignedTaskItem = ({ task }) => {
  const taskName = task.name;
  const project = task.project.name;
  const dueDate = task.dueDate;
  const outOfDate = task.outOfDate;
  const timeRemaining = task.timeRemaining;

  return (
    <>
      <Box sx={boxChildComponent} mb={3}>
        <Box
          display={"flex"}
          justifyContent={"space-between"}
          alignItems={"center"}
        >
          <Box display={"flex"} alignItems={"center"} mb={2}>
            <Box>
              <CategoryElement
                categoryId={task.taskCategory.categoryId}
                value={task.taskCategory.categoryName}
              />
            </Box>
            <Link to={`/tasks/${task.id}`} style={{ textDecoration: "none" }}>
              <Typography variant="body1">{taskName}</Typography>
            </Link>
          </Box>
          <Box>
            <StatusElement
              statusId={task.statusItem?.statusId}
              value={task.statusItem?.description}
            />
          </Box>
        </Box>

        <Box mb={2}>
          <Typography paragraph={true} variant="caption" color="primary">
            Dự án: {project}
          </Typography>
        </Box>
        <Box>
          <Grid container>
            <Grid item={true} xs={9}>
              <Box display={"flex"} alignItems={"center"}>
                <Box>
                  <Typography color="warning" variant="body2">
                    Thời hạn: {dueDate}
                  </Typography>
                </Box>
                <Box>
                  {outOfDate && <LocalFireDepartmentIcon color="error" />}
                </Box>
              </Box>
              <Typography variant="caption" sx={{ color: "red" }}>
                {timeRemaining}
              </Typography>
            </Grid>
            <Grid item={true} xs={3}></Grid>
          </Grid>
        </Box>
      </Box>
    </>
  );
};

AssignedTaskItem.propTypes = {
  task: PropTypes.object.isRequired,
};

export default AssignedTaskItem;
