import { Icon } from "@iconify/react";
import {
  AvatarGroup,
  Box,
  Card,
  Divider,
  LinearProgress,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import dayjs from "dayjs";
import { memo } from "react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { TaskCategory } from "../../components/task/category";
import { TaskPriority } from "../../components/task/priority";
import { TaskStatus } from "../../components/task/status";
import { useTaskContext } from "../../hooks/useTaskContext";
import { getDueDateColor, getProgressColor } from "../../utils/color.util";

const TitleWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  gap: 4,
  "& svg": {
    color: theme.palette.primary.main,
  },
}));

const TaskViewRight = () => {
  const { task } = useTaskContext();
  const { project } = useSelector((state) => state.project);
  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);

  const category = categoryStore.categories.find(
    (c) => c.categoryId === task.categoryId
  );
  const priority = priorityStore.priorities.find(
    (p) => p.priorityId === task.priorityId
  );
  const status = statusStore.statuses.find((s) => s.statusId === task.statusId);

  return (
    <Card sx={{ position: "sticky", top: 0, mr: 2 }}>
      <Box sx={{ p: 6 }}>
        <Typography>Chi tiết</Typography>

        {/* project */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2 }}>
          <TitleWrapper>
            <Icon icon="eos-icons:project-outlined"></Icon>
            <Typography sx={{ fontWeight: 600, fontSize: "0.875rem" }}>
              Dự án
            </Typography>
          </TitleWrapper>
          <Typography
            variant="body2"
            component={Link}
            to={`/project/${project.id}`}
            sx={{
              color: "text.primary",
              textDecoration: "none",
              "&:hover": {
                color: "primary.main",
                textDecoration: "underline",
              },
            }}
          >
            {project.name ?? "Không xác định"}
          </Typography>
        </Box>

        {/* assignees */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box
          sx={{
            mb: 2,
            display: "flex",
            flexDirection: "column",
            "& .MuiAvatarGroup-root": {
              flexDirection: "row",
            },
            "& .MuiAvatar-root": {
              fontSize: "0.95rem",
            },
          }}
        >
          <TitleWrapper>
            <Icon icon="iwwa:assign"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Phân công cho
            </Typography>
          </TitleWrapper>
          <AvatarGroup max={10} className="pull-up">
            {task.assignee ? (
              <Tooltip
                title={`${task.assignee.firstName ?? ""} ${
                  task.assignee.lastName ?? ""
                }`}
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 4 }}>
                  <UserAvatar
                    user={task.assignee}
                    sx={{
                      width: 35,
                      height: 35,
                      cursor: "pointer",
                      "&:not(:last-child)": {
                        mr: -2,
                        border: (theme) =>
                          `2px solid ${theme.palette.background.paper}`,
                      },
                    }}
                  />
                  <Typography variant="body2" sx={{ fontWeight: 550 }} noWrap>
                    {`${task.assignee.firstName ?? ""} ${
                      task.assignee.lastName ?? ""
                    }`}
                  </Typography>
                </Box>
              </Tooltip>
            ) : (
              <Typography> - </Typography>
            )}
          </AvatarGroup>
        </Box>

        {/* Estimated time */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2, display: "flex" }}>
          <TitleWrapper>
            <Icon icon="guidance:time"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Ước tính
            </Typography>
          </TitleWrapper>
          <Typography variant="body2">
            {task.estimatedTime !== null ? `${task.estimatedTime}` : " - "}
            {" (giờ)"}
          </Typography>
        </Box>

        {/* Progress */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2 }}>
          <TitleWrapper>
            <Icon icon="game-icons:progression"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Tiến độ
            </Typography>
          </TitleWrapper>
          <Box sx={{ width: "100%" }}>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
              {task.progress ?? 0}%
            </Typography>
            <LinearProgress
              variant="determinate"
              value={task.progress ?? 0}
              color={getProgressColor(task.progress)}
              sx={{ height: 6, borderRadius: "5px" }}
            />
          </Box>
        </Box>

        {/* start date */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2 }}>
          <TitleWrapper>
            <Icon icon="mdi:calendar-start"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Ngày bắt đầu
            </Typography>
          </TitleWrapper>
          <Typography variant="body2">
            {task.fromDate ? dayjs(task.fromDate).format("DD/MM/YYYY") : " - "}
          </Typography>
        </Box>

        {/* due date */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2 }}>
          <TitleWrapper>
            <Icon icon="tabler:calendar-due"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Thời hạn
            </Typography>
          </TitleWrapper>
          <Typography
            variant="body2"
            sx={{ color: getDueDateColor(task.dueDate) }}
          >
            {task.dueDate ? dayjs(task.dueDate).format("DD/MM/YYYY") : " - "}
          </Typography>
        </Box>

        {/* status */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2, display: "flex" }}>
          <TitleWrapper>
            <Icon icon="f7:status"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Trạng thái
            </Typography>
          </TitleWrapper>
          {status && <TaskStatus status={status} />}
        </Box>

        {/* priority */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2, display: "flex" }}>
          <TitleWrapper>
            <Icon icon="iconoir:priority-high"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Ưu tiên
            </Typography>
          </TitleWrapper>
          {priority && <TaskPriority priority={priority} showText />}
        </Box>

        {/* category */}
        <Divider sx={{ my: (theme) => `${theme.spacing(2)} !important` }} />
        <Box sx={{ mb: 2, display: "flex" }}>
          <TitleWrapper>
            <Icon icon="material-symbols:category-rounded"></Icon>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Danh mục
            </Typography>
          </TitleWrapper>
          {category && <TaskCategory category={category} />}
        </Box>
      </Box>
    </Card>
  );
};

export default memo(TaskViewRight);
