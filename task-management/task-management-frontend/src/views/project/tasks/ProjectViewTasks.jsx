import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Card,
  CardContent,
  InputAdornment,
  LinearProgress,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import AvatarGroup from "@mui/material/AvatarGroup";
import { DataGrid } from "@mui/x-data-grid";
import dayjs from "dayjs";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskCategory } from "../../../components/task/category";
import { TaskPriority } from "../../../components/task/priority";
import { TaskStatus } from "../../../components/task/status";
import { useDebounce } from "../../../hooks/useDebounce";
import {
  clearCache,
  fetchTasks,
  resetPagination,
  resetSort,
  setPagination,
  setSort,
  setSearch as setSearchAction,
  setFilters,
} from "../../../store/project/tasks";
import { getDueDateColor, getProgressColor } from "../../../utils/color.util";
import { DialogAddTask } from "./DialogAddTask";
import { Filter } from "./Filter";
import { buildFilterString } from "../../../utils/task-filter";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";

const ProjectViewTasks = () => {
  const dispatch = useDispatch();
  const { project } = useSelector((state) => state.project);
  const {
    filters,
    sort,
    pagination,
    fetchLoading,
    totalCount,
    tasksCache,
    search: searchStore,
  } = useSelector((state) => state.tasks);
  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);
  const { members } = useSelector((state) => state.project);

  const [search, setSearch] = useState(searchStore);
  const searchDebounce = useDebounce(search, 1000);

  const [rows, setRows] = useState([]);
  const [openAddTask, setOpenAddTask] = useState(false);

  const [isInitialized, setIsInitialized] = useState(false);

  const { ref, updateHeight } = usePreventOverflow();

  const columns = [
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
                to={`/project/${project.id}/task/${row.id}`}
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
            <Tooltip
              title={`${row.assignee?.firstName} ${row.assignee?.lastName}`}
              key={row.assignee?.id}
            >
              <UserAvatar user={row.assignee} />
            </Tooltip>
          </AvatarGroup>
        ) : (
          <Typography> - </Typography>
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
    },
  ];

  const handleSortModel = (newModel) => {
    if (newModel.length) {
      dispatch(setSort(newModel[0]));
    } else {
      dispatch(resetSort());
    }
  };

  const handlePaginationModel = (newModel) => {
    if (
      newModel.page === pagination.page &&
      newModel.pageSize !== pagination.size
    ) {
      dispatch(clearCache());
    }

    dispatch(
      setPagination({
        page: newModel.page,
        size: newModel.pageSize,
      })
    );
  };

  const buildQueryString = useCallback(() => {
    const builder = [];
    const encodedSearch = encodeURIComponent(searchStore).replace(
      /%20/g,
      "%1F"
    );
    builder.push(
      encodedSearch
        ? `( name:*${encodedSearch}* OR description:*${encodedSearch}* )`
        : ""
    );

    builder.push(buildFilterString(filters));

    return builder.filter((s) => s !== "").join(" AND ");
  }, [searchStore, filters]);

  const fetchTasksPagination = useCallback(async () => {
    if (!tasksCache[pagination.page]) {
      try {
        await dispatch(
          fetchTasks({
            projectId: project.id,
            filters: {
              ...pagination,
              q: buildQueryString(),
              sort: `${sort.field},${sort.sort}`,
            },
          })
        );
      } catch (error) {
        console.log(error);
        toast.error("Có lỗi khi lấy danh sách nhiệm vụ");
      }
    }
  }, [dispatch, pagination, project.id, sort, buildQueryString]);

  const onSearch = async () => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setSearchAction(searchDebounce));
  };

  const onFilter = async (filters) => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setFilters(filters));
  };

  useEffect(() => {
    fetchTasksPagination();
  }, [fetchTasksPagination]);

  useEffect(() => {
    if (tasksCache[pagination.page]) {
      setRows(tasksCache[pagination.page]);
    }
  }, [pagination.page, tasksCache]);

  useEffect(() => {
    if (isInitialized) {
      onSearch();
    } else {
      setIsInitialized(true);
    }
  }, [searchDebounce]);

  useEffect(() => {
    updateHeight(10);
  }, [window.innerHeight]);

  return (
    <Card>
      {/* Header */}
      <CardContent
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <Box sx={{ display: "flex", gap: 4, alignItems: "center" }}>
          <Typography variant="h5">{totalCount ?? 0} nhiệm vụ</Typography>
          <Tooltip title="Lọc">
            <Filter
              text="Filters"
              onFilter={onFilter}
              filters={filters}
              members={members.map((m) => m.member)}
            />
          </Tooltip>
        </Box>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
          }}
        >
          <TextField
            size="small"
            placeholder="Tìm kiếm nhiệm vụ"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Icon icon="material-symbols:search" />
                </InputAdornment>
              ),
            }}
          />
          <Button
            variant="outlined"
            sx={{ textTransform: "normal", ml: 4 }}
            onClick={() => setOpenAddTask(true)}
          >
            Thêm nhiệm vụ
          </Button>
        </Box>
      </CardContent>

      {/* Table */}
      <Box ref={ref}>
        <DataGrid
          rows={rows}
          columns={columns}
          disableRowSelectionOnClick
          pageSizeOptions={[10, 20, 50]}
          paginationModel={{
            page: pagination.page,
            pageSize: pagination.size,
          }}
          onPaginationModelChange={handlePaginationModel}
          pagination
          paginationMode="server"
          rowCount={totalCount}
          loading={fetchLoading}
          rowHeight={68}
          onSortModelChange={handleSortModel}
          localeText={{
            noRowsLabel: "Không có dữ liệu",
          }}
        />
      </Box>
      <DialogAddTask open={openAddTask} setOpen={setOpenAddTask} />
    </Card>
  );
};

export { ProjectViewTasks };
