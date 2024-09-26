import { Box, Typography } from "@mui/material";
import dayjs from "dayjs";
import PropTypes from "prop-types";
import { useSelector } from "react-redux";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskCategory } from "../../../components/task/category";
import { TaskPriority } from "../../../components/task/priority";
import { TaskStatus } from "../../../components/task/status";
const TaskPreview = ({ task }) => {
  const { priority, status, category } = useSelector((state) => state);

  return (
    <Box
      sx={{
        border: "1px solid #e0e0e0",
        borderRadius: "6px",
        backgroundColor: (theme) => theme.palette.background.paper,
        p: 2,
        boxShadow: 2,
      }}
    >
      <Box sx={{ mb: 1, display: "flex" }}>
        {task.priorityId && (
          <TaskPriority
            priority={priority.priorities.find(
              (p) => p.priorityId === task.priorityId
            )}
            showText={false}
          />
        )}
        <Typography fontWeight={650} variant="body1" fontSize="1.125rem">
          {task.name}
        </Typography>
      </Box>
      <Box sx={{ display: "flex", gap: 1 }}>
        <Typography>Người thực hiện:</Typography>
        {task.assignee && (
          <UserAvatar
            user={task.assignee}
            width={24}
            height={24}
            fontSize="0.7rem"
          />
        )}
      </Box>
      {task.statusId && (
        <Typography>
          Trạng thái:{" "}
          <TaskStatus
            status={status.statuses.find((s) => s.statusId === task.statusId)}
          />
        </Typography>
      )}
      {task.categoryId && (
        <Typography>
          Thể loại:{" "}
          <TaskCategory
            category={category.categories.find(
              (c) => c.categoryId === task.categoryId
            )}
          />
        </Typography>
      )}
      <Typography>
        Bắt đầu:{" "}
        {task.fromDate && dayjs(task.fromDate).format("DD/MM/YYYY HH:mm")}
      </Typography>
      <Typography>
        Tới hạn:{" "}
        {task.dueDate && dayjs(task.dueDate).format("DD/MM/YYYY HH:mm")}
      </Typography>
      <Box>
        <Typography>Tiến độ: {task.progress}%</Typography>
      </Box>
    </Box>
  );
};

TaskPreview.propTypes = {
  task: PropTypes.object.isRequired,
};

export { TaskPreview };
