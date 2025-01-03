import { Box, Typography, styled } from "@mui/material";
import MuiTimeline from "@mui/lab/Timeline";
import { TaskLogItem } from "./TaskLogItem";
import { useTaskContext } from "../../../hooks/useTaskContext";

const Timeline = styled(MuiTimeline)({
  paddingLeft: 0,
  paddingRight: 0,
  "& .MuiTimelineItem-root": {
    width: "100%",
    "&:before": {
      display: "none",
    },
  },
});

const TaskViewLog = () => {
  const { logs } = useTaskContext();

  if (logs?.length <= 0) {
    return (
      <Box sx={{ p: 2 }}>
        <Typography variant="subtitle2" sx={{ fontStyle: "italic" }}>
          Không có lịch sử hoạt động
        </Typography>
      </Box>
    );
  }
  return (
    <Timeline sx={{ my: 0, py: 0, mb: 4 }}>
      {logs
        .slice()
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .map((log) => (
          <TaskLogItem key={log.id} item={log} />
        ))}
    </Timeline>
  );
};

export default TaskViewLog;
