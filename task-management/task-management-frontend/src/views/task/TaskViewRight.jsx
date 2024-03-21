import {
  AvatarGroup,
  Box,
  Card,
  Divider,
  Tooltip,
  Typography,
  Skeleton,
  styled,
  LinearProgress,
} from "@mui/material";
import dayjs from "dayjs";
import { memo } from "react";
import { Link } from "react-router-dom";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import CustomChip from "../../components/mui/chip";
import { useProjectContext } from "../../hooks/useProjectContext";
import { useTaskContext } from "../../hooks/useTaskContext";
import {
  getCategoryColor,
  getDueDateColor,
  getPriorityColor,
  getProgressColor,
  getRandomColorSkin,
  getStatusColor,
} from "../../utils/color.util";
import { Icon } from "@iconify/react";

const TitleWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  gap: 4,
  "& svg": {
    color: theme.palette.primary.main,
  },
}));

const Loading = () => (
  <Card>
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

const TaskViewRight = () => {
  const { isLoading: taskLoading, task } = useTaskContext();
  const {
    isLoading: projectLoading,
    project,
    statuses,
    priorities,
    categories,
  } = useProjectContext();

  if (taskLoading || projectLoading) return <Loading />;

  const category = categories.find((c) => c.categoryId === task.categoryId);
  const priority = priorities.find((p) => p.priorityId === task.priorityId);
  const status = statuses.find((s) => s.statusId === task.statusId);

  return (
    <Card>
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
                  <CustomAvatar
                    skin="light"
                    color={getRandomColorSkin(task.assignee.id)}
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
                  >
                    {`${task.assignee.firstName?.charAt(0) ?? ""}${
                      task.assignee.lastName?.charAt(0) ?? ""
                    }`}{" "}
                  </CustomAvatar>
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
          {status && (
            <CustomChip
              size="small"
              skin="light"
              label={status.description}
              color={getStatusColor(status.statusId)}
            />
          )}
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
          {priority && (
            <CustomChip
              size="small"
              skin="light"
              label={priority.priorityName}
              color={getPriorityColor(priority.priorityId)}
            />
          )}
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
          {category && (
            <CustomChip
              size="small"
              skin="light"
              label={category.categoryName}
              color={getCategoryColor(category.categoryId)}
            />
          )}
        </Box>
      </Box>
    </Card>
  );
};

export default memo(TaskViewRight);
