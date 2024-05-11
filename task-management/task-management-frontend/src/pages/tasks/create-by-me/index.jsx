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
import { Helmet } from "react-helmet";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskCategory } from "../../../components/task/category";
import { TaskStatus } from "../../../components/task/status";
import { useDebounce } from "../../../hooks/useDebounce";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import {
  clearCache,
  fetchAllAssignees,
  fetchCreatedTasks,
  resetPagination,
  resetSort,
  setFilters,
  setPagination,
  setSearch as setSearchAction,
  setSort,
} from "../../../store/created-tasks";
import { getDueDateColor, getProgressColor } from "../../../utils/color.util";
import { buildFilterString } from "../../../utils/task-filter";
import { Filter } from "../../../views/project/tasks/Filter";

const TaskCreated = () => {
  const {
    tasksCache,
    totalCount,
    search: searchStore,
    filters,
    pagination,
    sort,
    fetchLoading,
    assignees,
  } = useSelector((state) => state.createdTasks);
  const { statuses } = useSelector((state) => state.status);
  const dispatch = useDispatch();

  const [rows, setRows] = useState(tasksCache[pagination.page] ?? []);
  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 1000);

  const { ref, updateHeight } = usePreventOverflow();

  const [isFirstFetch, setIsFirstFetch] = useState(true);

  /**
   * @type {import("@mui/x-data-grid").GridColDef[]}
   */
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
      display: "flex",
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
      display: "flex",
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
      display: "flex",
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
      display: "flex",
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
      display: "flex",
    },
    {
      flex: 0.1,
      field: "assignee",
      headerName: "Gán cho",
      align: "center",
      headerAlign: "center",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) =>
        row.assignee && (
          <AvatarGroup max={3} className="pull-up">
            <UserAvatar user={row.assignee} key={row.assignee.id} />
          </AvatarGroup>
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
  ];

  const handleSortModel = (newModel) => {
    if (newModel.length > 0) {
      dispatch(setSort({ field: newModel[0].field, sort: newModel[0].sort }));
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
        ? `( name:*${encodedSearch}* OR projectName:*${encodedSearch}* OR description:*${encodedSearch}* )`
        : ""
    );

    builder.push(buildFilterString(filters));

    return builder.filter((s) => s !== "").join(" AND ");
  }, [searchStore, filters]);

  const getTasks = useCallback(async () => {
    if (tasksCache[pagination.page]) return;
    try {
      await dispatch(
        fetchCreatedTasks({
          ...pagination,
          search: buildQueryString(),
          sort: `${sort.field},${sort.sort}`,
        })
      );
    } catch (error) {
      toast.error("Lỗi khi lấy dữ liệu");
      console.log(error);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination, sort, buildQueryString, dispatch]);

  const getAssignees = useCallback(async () => {
    try {
      if (assignees.length <= 0) await dispatch(fetchAllAssignees());
    } catch (error) {
      toast.error("Lỗi khi lấy dữ liệu");
      console.log(error);
    }
  }, [dispatch]);

  const onSearch = async () => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setSearchAction(searchDebounce));
  };

  const onFilter = async (filter) => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setFilters(filter));
  };

  useEffect(() => {
    getTasks();
  }, [getTasks]);

  useEffect(() => {
    getAssignees();
  }, [getAssignees]);

  useEffect(() => {
    if (tasksCache[pagination.page]) {
      setRows(tasksCache[pagination.page]);
    }
  }, [tasksCache, pagination.page]);

  useEffect(() => {
    if (isFirstFetch) {
      setIsFirstFetch(false);
    } else {
      onSearch();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchDebounce]);

  useEffect(() => {
    updateHeight(10);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.innerHeight]);

  return (
    <>
      <Helmet>
        <title>Danh sách công việc đã tạo | Task management</title>
      </Helmet>
      <Card sx={{ mr: 2 }}>
        <CardContent sx={{ display: "flex", justifyContent: "space-between" }}>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
            <Typography variant="h6" component="div">
              {totalCount} nhiệm vụ đã tạo
            </Typography>
            <Tooltip title="Lọc">
              <Filter
                onFilter={onFilter}
                filters={filters}
                text="Filters"
                sx={{
                  minWidth: 0,
                  borderRadius: "11px",
                  padding: (theme) => theme.spacing(0, 2),
                  "& svg": {
                    fontSize: "1rem !important",
                  },
                }}
                excludeFields={["creatorId"]}
                members={assignees}
              />
            </Tooltip>
          </Box>
          <Box>
            <TextField
              size="small"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Tìm kiếm..."
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
        <Box ref={ref}>
          <DataGrid
            rows={rows}
            loading={fetchLoading}
            rowCount={totalCount}
            columns={columns}
            pageSizeOptions={[10, 25, 50]}
            pagination
            paginationMode="server"
            paginationModel={{
              page: pagination.page,
              pageSize: pagination.size,
            }}
            onPaginationModelChange={handlePaginationModel}
            onSortModelChange={handleSortModel}
            rowHeight={70}
            localeText={{
              noRowsLabel: "Không có dữ liệu",
            }}
          />
        </Box>
      </Card>
    </>
  );
};

export default TaskCreated;
