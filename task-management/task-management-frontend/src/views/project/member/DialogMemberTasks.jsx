import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  AvatarGroup,
  LinearProgress,
  Box,
  Tooltip,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import { useNavigate } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskPriority } from "../../../components/task/priority";
import { TaskCategory } from "../../../components/task/category";
import { TaskStatus } from "../../../components/task/status";
import { getDueDateColor, getProgressColor } from "../../../utils/color.util";
import dayjs from "dayjs";
import { useSelector } from "react-redux";

const DialogMemberTasks = ({ open, onClose, tasks, projectId, loading }) => {
  const taskColumns = [
    {
      flex: 0.3,
      field: "name",
      headerName: "Tên",
      filterable: false,
      renderCell: ({ row }) => {
        const category = categoryStore.categories.find(
          (category) => category.categoryId === row.categoryId
        );
        const priority = priorityStore.priorities.find(
          (priority) => priority.priorityId === row.priorityId
        );
        return (
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
            <Tooltip title={row.name}>
              <Typography
                variant="subtitle2"
                component={Link}
                to={`/project/${projectId}/task/${row.id}`}
                noWrap
                sx={{
                  ml: 2,
                  color: "text.primary",
                  fontWeight: 650,
                  fontSize: "1rem",
                  textDecoration: "none",
                  "&:hover": {
                    textDecoration: "underline",
                    color: "primary.main",
                  },
                  // textTransform: "capitalize",
                }}
              >
                {row.name}
              </Typography>
            </Tooltip>
            <Box sx={{ display: "flex", gap: 1 }}>
              {priority && <TaskPriority priority={priority} />}
              {category && <TaskCategory category={category} />}
            </Box>
          </Box>
        );
      },
      display: "flex",
    },
    {
      flex: 0.2,
      minWidth: 100,
      field: "status",
      headerName: "Trạng thái",
      align: "center",
      headerAlign: "center",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) => {
        const status = statusStore.statuses.find(
          (status) => status.statusId === row.statusId
        );
        return status ? (
          <TaskStatus status={status} />
        ) : (
          <Typography variant="body2" sx={{ color: "text.primary" }}>
            -
          </Typography>
        );
      },
      display: "flex",
    },
    {
      flex: 0.2,
      minWidth: 100,
      field: "progress",
      headerName: "Tiến độ",
      align: "center",
      headerAlign: "center",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) => {
        const progress = row.progress ?? 0;
        return (
          <Box sx={{ display: "flex", flexDirection: "column", width: "100%" }}>
            <Typography variant="body2" sx={{ color: "text.primary" }}>
              {progress}%
            </Typography>
            <LinearProgress
              variant="determinate"
              value={progress}
              color={getProgressColor(progress)}
              sx={{ height: 6, borderRadius: "5px", width: "100%" }}
            />
          </Box>
        );
      },
      display: "flex",
    },
    {
      flex: 0.15,
      minWidth: 100,
      field: "assignee",
      headerName: "Phân công cho",
      align: "center",
      headerAlign: "center",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) =>
        row.assignee ? (
          <AvatarGroup max={3} className="pull-up">
            <UserAvatar user={row.assignee} key={row.assignee?.id} />
          </AvatarGroup>
        ) : (
          <Typography> - </Typography>
        ),
      display: "flex",
    },
    {
      flex: 0.1,
      minWidth: 100,
      field: "createdStamp",
      headerName: "Ngày tạo",
      filterable: false,
      renderCell: ({ row }) => (
        <Typography variant="body2" sx={{ color: "text.primary" }}>
          {dayjs(row.createdStamp).format("DD/MM/YYYY") ?? ""}
        </Typography>
      ),
      display: "flex",
    },
    {
      flex: 0.1,
      minWidth: 100,
      field: "dueDate",
      headerName: "Hạn",
      align: "center",
      headerAlign: "center",
      filterable: false,
      renderCell: ({ row }) => (
        <Typography
          variant="body2"
          sx={{ color: getDueDateColor(row.dueDate) }}
        >
          {row.dueDate ? dayjs(row.dueDate).format("DD/MM/YYYY") : " - "}
        </Typography>
      ),
      display: "flex",
    },
  ];

  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);

  const navigate = useNavigate();
  const handleRowClick = (taskId) => {
    navigate(`/project/${projectId}/task/${taskId}`);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="lg" fullWidth>
      <DialogTitle>Danh sách nhiệm vụ</DialogTitle>
      <DialogContent style={{ height: 400 }}>
        <div style={{ display: "flex", height: "100%" }}>
          <div style={{ flexGrow: 1 }}>
            <DataGrid
              rows={tasks}
              columns={taskColumns}
              onRowClick={(params) => handleRowClick(params.row.id)}
              loading={loading}
            />
          </div>
        </div>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Đóng
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DialogMemberTasks.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  tasks: PropTypes.arrayOf(PropTypes.object).isRequired,
  projectId: PropTypes.string.isRequired,
  loading: PropTypes.bool.isRequired,
};

export { DialogMemberTasks };
