import { Icon } from "@iconify/react";
import { Box } from "@mui/material";
import PropTypes from "prop-types";
import { getPriorityColor } from "../../../utils/color.util";

const TaskPriority = ({
  priority,
  width = 20,
  height = 20,
  showText = false,
}) => {
  return (
    <Box
      sx={{
        color: (theme) =>
          theme.palette[getPriorityColor(priority.priorityId)]?.main,
        display: "flex",
        gap: 1,
        alignItems: "center",
      }}
      title={priority.priorityName}
    >
      <Icon
        size="small"
        icon="flowbite:flag-solid"
        width={width}
        height={height}
      />
      {showText && <span>{priority.priorityName}</span>}
    </Box>
  );
};

TaskPriority.propTypes = {
  priority: PropTypes.object,
  width: PropTypes.number,
  height: PropTypes.number,
  showText: PropTypes.bool,
};

export { TaskPriority };
