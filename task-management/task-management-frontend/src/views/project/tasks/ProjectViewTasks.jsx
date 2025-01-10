import { Icon } from "@iconify/react";
import {
  Box,
  InputAdornment,
  LinearProgress,
  TextField,
  Tooltip,
  Typography,
  Card,
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
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import {
  clearCache,
  fetchTasks,
  resetPagination,
  resetSort,
  setFilters,
  setPagination,
  setSearch as setSearchAction,
  setSort,
} from "../../../store/project/tasks";
import { getDueDateColor, getProgressColor } from "../../../utils/color.util";
import { buildFilterString } from "../../../utils/task-filter";
import { Filter } from "./Filter";
import { CustomPagination } from "../../../components/mui/table/CustomPagination";

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

  const [rows, setRows] = useState(tasksCache[pagination.page] ?? []);

  const [isInitialized, setIsInitialized] = useState(false);

  const { ref, updateHeight } = usePreventOverflow();

  /**
   * @type {import("@mui/x-data-grid").GridColDef[]}
   */
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
    <Box>
      {/* Header */}
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          mb: 2,
        }}
      >
        <Box sx={{ display: "flex", gap: 4, alignItems: "center" }}>
          <Typography variant="h6">{totalCount ?? 0} nhiệm vụ</Typography>
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
            sx={{
              "& .MuiInputBase-root": {
                height: "34px",
                fontSize: "14px",
              },
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Icon icon="material-symbols:search" />
                </InputAdornment>
              ),
            }}
          />
        </Box>
      </Box>
      {/* Table */}
      <Card ref={ref}>
        <DataGrid
          rows={rows}
          columns={columns}
          disableRowSelectionOnClick
          pagination
          paginationMode="server"
          rowCount={totalCount}
          loading={fetchLoading}
          rowHeight={68}
          onSortModelChange={handleSortModel}
          localeText={{
            noRowsLabel: "Không có dữ liệu",
          }}
          slots={{
            pagination: CustomPagination,
          }}
          slotProps={{
            pagination: {
              page: pagination.page + 1,
              rowsPerPage: pagination.size,
              count: Math.ceil(totalCount / pagination.size),
              onPageChange: (e, page) => {
                handlePaginationModel({
                  page: page - 1,
                  pageSize: pagination.size,
                });
              },
              onRowsPerPageChange: (value) => {
                handlePaginationModel({
                  page: 0,
                  pageSize: value,
                });
              },
              pageSizeOptions: [10, 20, 50],
              showFirstButton: true,
              showLastButton: true,
            },
          }}
        />
      </Card>
    </Box>
  );
};

export { ProjectViewTasks };
