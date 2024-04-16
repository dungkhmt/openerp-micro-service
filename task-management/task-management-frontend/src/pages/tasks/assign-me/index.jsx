import { Icon } from "@iconify/react";
import {
  AvatarGroup,
  Box,
  Card,
  CardContent,
  IconButton,
  LinearProgress,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import dayjs from "dayjs";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskCategory } from "../../../components/task/category";
import { TaskStatus } from "../../../components/task/status";
import { useDebounce } from "../../../hooks/useDebounce";
import { StatusService } from "../../../services/api/task-status.service";
import { TaskService } from "../../../services/api/task.service";
import { getDueDateColor, getProgressColor } from "../../../utils/color.util";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const DEFAULT_SORT_MODEL = {
  field: "createdStamp",
  sort: "desc",
};

const TaskAssigned = () => {
  const [rows, setRows] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );
  const [sortModel, setSortModel] = useState(DEFAULT_SORT_MODEL);
  const [isLoading, setIsLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [statuses, setStatuses] = useState([]);
  const searchDebounce = useDebounce(search, 1000);

  const columns = [
    {
      flex: 0.25,
      field: "name",
      headerName: "Tên",
      filterable: false,
      renderCell: ({ row }) => (
        <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
          <Tooltip title={row.name}>
            <Typography
              variant="subtitle2"
              component={Link}
              to={`/project/${row.project.id}/task/${row.id}`}
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
          {row.category && <TaskCategory category={row.category} />}
        </Box>
      ),
    },
    {
      flex: 0.15,
      field: "project",
      headerName: "Dự án",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) =>
        row.project && (
          <Tooltip title={row.project.name}>
            <Typography
              noWrap
              variant="body2"
              sx={{
                color: "text.primary",
                fontWeight: 600,
                textDecoration: "none",
                "&:hover": {
                  textDecoration: "underline",
                  color: "primary.light",
                },
              }}
              component={Link}
              to={`/project/${row.project.id}/overview`}
            >
              {row.project.name}
            </Typography>
          </Tooltip>
        ),
    },
    {
      flex: 0.1,
      field: "status",
      headerName: "Trạng thái",
      align: "center",
      headerAlign: "center",
      sortable: false,
      renderCell: ({ row }) => {
        const status = statuses?.find((s) => s.statusId === row.statusId);
        return status && <TaskStatus status={status} />;
      },
    },
    {
      flex: 0.1,
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
    },
    {
      flex: 0.1,
      field: "dueDate",
      headerName: "Thời hạn",
      align: "center",
      headerAlign: "center",
      renderCell: ({ row }) => (
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <Typography variant="body2" color={getDueDateColor(row.dueDate)}>
            {row.dueDate ? dayjs(row.dueDate).format("DD/MM/YYYY") : " - "}
          </Typography>
        </Box>
      ),
    },
    {
      flex: 0.1,
      field: "creator",
      headerName: "Người tạo",
      align: "center",
      headerAlign: "center",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) =>
        row.creator && (
          <AvatarGroup max={3} className="pull-up">
            <Tooltip
              title={`${row.creator.firstName} ${row.creator.lastName}`}
              key={row.creator.id}
            >
              <UserAvatar user={row.creator} />
            </Tooltip>
          </AvatarGroup>
        ),
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
    },
  ];

  const handleSortModel = (newModel) => {
    if (newModel.length > 0) {
      setSortModel(newModel[0]);
    } else {
      setSortModel(DEFAULT_SORT_MODEL);
    }
  };

  const getStatuses = useCallback(async () => {
    try {
      const res = await StatusService.getStatuses();
      setStatuses(res);
    } catch (error) {
      toast.error("Lỗi khi lấy dữ liệu");
    }
  }, []);

  const getTasks = useCallback(async () => {
    try {
      setIsLoading(true);
      const encodedSearch = encodeURIComponent(searchDebounce).replace(
        /%20/g,
        "%1F"
      );
      const response = await TaskService.getAssignedTasks({
        page: paginationModel.page,
        size: paginationModel.pageSize,
        search: encodedSearch
          ? `name:*${encodedSearch}* OR projectName:*${encodedSearch}*`
          : "",
        sort: `${sortModel.field},${sortModel.sort}`,
      });
      setRows(response.data);
      setTotalCount(response.totalElements);
    } catch (error) {
      toast.error("Lỗi khi lấy dữ liệu");
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  }, [paginationModel, searchDebounce, sortModel]);

  useEffect(() => {
    getTasks();
  }, [getTasks]);

  useEffect(() => {
    getStatuses();
  }, [getStatuses]);

  return (
    <Card>
      <CardContent sx={{ display: "flex", justifyContent: "space-between" }}>
        <Typography variant="h6" component="div">
          Danh sách các nhiệm vụ được giao
        </Typography>
        <Box>
          <TextField
            size="small"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search…"
            InputProps={{
              startAdornment: (
                <Box sx={{ mr: 2, display: "flex" }}>
                  <Icon icon="mdi:magnify" fontSize={20} />
                </Box>
              ),
              endAdornment: (
                <IconButton
                  size="small"
                  title="Clear"
                  aria-label="Clear"
                  onClick={() => setSearch("")}
                >
                  <Icon icon="mdi:close" fontSize={20} />
                </IconButton>
              ),
            }}
            sx={{
              width: {
                xs: 1,
                sm: "auto",
              },
              "& .MuiInputBase-root > svg": {
                mr: 2,
              },
            }}
          />
        </Box>
      </CardContent>
      <DataGrid
        rows={rows}
        loading={isLoading}
        rowCount={totalCount}
        columns={columns}
        pageSizeOptions={[10, 25, 50]}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        onSortModelChange={handleSortModel}
        sx={{ height: "75vh" }}
        rowHeight={70}
      />
    </Card>
  );
};

export default TaskAssigned;
