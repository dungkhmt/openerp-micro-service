import { Box, Typography, IconButton, Tooltip } from "@mui/material";
import CategoryElement from "../common/CategoryElement";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import LocalFireDepartmentIcon from "@mui/icons-material/LocalFireDepartment";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";

const BoardItem = ({ task }) => {
  return (
    <>
      <Box
        border={"1px solid"}
        borderRadius={"5px"}
        borderColor={"#bdbdbd"}
        px={2}
        mb={2}
      >
        <Box
          display={"flex"}
          justifyContent={"space-between"}
          alignItems={"center"}
        >
          <CategoryElement
            categoryId={task.taskCategory.categoryId}
            value={task.taskCategory.categoryName}
          />
          <IconButton
            aria-label="delete"
            size="large"
            onClick={() => alert("click r nha!")}
          >
            <SettingsOutlinedIcon />
          </IconButton>
        </Box>
        <Box>
          <Link to={`/tasks/${task.id}`} style={{ textDecoration: "none" }}>
            <Typography variant="body2" color="primary">
              {task.name}
            </Typography>
          </Link>
        </Box>
        <Box display={"flex"} alignItems={"center"}>
          <Tooltip title={task.assignee}>
            <AccountCircleIcon sx={{ marginRight: "5px" }} color={"info"} />
          </Tooltip>
          <Typography variant="caption" color="info">
            {task.dueDate.split(" ")[0]}
          </Typography>
          <Box>
            {task.outOfDate && (
              <LocalFireDepartmentIcon color="error" fontSize={"small"} />
            )}
          </Box>
        </Box>
      </Box>
    </>
  );
};

BoardItem.propTypes = {
  task: PropTypes.object,
};

export default BoardItem;
