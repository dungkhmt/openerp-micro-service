import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Card,
  CardContent,
  Collapse,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  LinearProgress,
  MenuItem,
  Select,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import AvatarGroup from "@mui/material/AvatarGroup";
import { DataGrid } from "@mui/x-data-grid";
import dayjs from "dayjs";
import { useEffect, useState, useCallback } from "react";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import CustomChip from "../../../components/mui/chip";
import { useDebounce } from "../../../hooks/useDebounce";
import {
  fetchTasks,
  clearCache,
  resetSort,
  setFilters,
  setPagination,
  setSort,
  resetPagination,
  resetFilters,
} from "../../../store/project/tasks";
import {
  getCategoryColor,
  getDueDateColor,
  getPriorityColor,
  getProgressColor,
  getStatusColor,
} from "../../../utils/color.util";
import { DialogAddTask } from "./DialogAddTask";

const ProjectViewTasks = () => {
  const dispatch = useDispatch();
  const { members, project } = useSelector((state) => state.project);
  const { filters, sort, pagination, fetchLoading, totalCount, tasksCache } =
    useSelector((state) => state.tasks);
  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);

  const [toggleFilter, setToggleFilter] = useState(false);
  const [filter, setFilter] = useState(filters);

  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 1000);

  const [rows, setRows] = useState([]);
  const [openAddTask, setOpenAddTask] = useState(false);

  const [isInitialized, setIsInitialized] = useState(false);

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
              {category && (
                <CustomChip
                  size="small"
                  skin="light"
                  sx={{ width: "fit-content" }}
                  label={category.categoryName}
                  color={getCategoryColor(category.categoryId)}
                />
              )}
              {priority && (
                <CustomChip
                  size="small"
                  skin="light"
                  label={priority.priorityName}
                  color={getPriorityColor(priority.priorityId)}
                />
              )}
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
          <CustomChip
            size="small"
            skin="light"
            label={status.description}
            color={getStatusColor(status.statusId)}
          />
        ) : (
          <Typography variant="body2" sx={{ color: "text.primary" }}>
            Không xác định
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

  const buildQueryString = () => {
    const builder = [];
    const encodedSearch = encodeURIComponent(searchDebounce).replace(
      /%20/g,
      "%1F"
    );
    builder.push(
      encodedSearch
        ? `( name:*${encodedSearch}* OR description:*${encodedSearch}* )`
        : ""
    );
    Object.entries(filter).forEach(([key, value]) => {
      if (value) {
        builder.push(`${key}:${value}`);
      }
    });

    return builder.filter((s) => s !== "").join(" AND ");
  };

  const fetchTasksPagination = useCallback(async () => {
    if (!tasksCache[pagination.page]) {
      try {
        await dispatch(
          fetchTasks({
            projectId: project.id,
            filters: {
              ...filters,
              ...pagination,
              sort: `${sort.field},${sort.sort}`,
            },
          })
        );
      } catch (error) {
        console.log(error);
        toast.error("Có lỗi khi lấy danh sách nhiệm vụ");
      }
    }
  }, [dispatch, filters, pagination, project.id, sort]);

  const onFilter = async () => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(
      setFilters({
        ...filter,
        q: buildQueryString(),
      })
    );
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
      onFilter();
    } else {
      setIsInitialized(true);
    }
  }, [searchDebounce]);

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
          <Typography variant="h5">Danh sách nhiệm vụ</Typography>
          <Tooltip title="Lọc">
            <IconButton
              color="secondary"
              size="medium"
              onClick={() => setToggleFilter(!toggleFilter)}
            >
              {toggleFilter ? (
                <Icon icon="ic:baseline-filter-alt" fontSize="inherit" />
              ) : (
                <Icon icon="ic:baseline-filter-alt-off" fontSize="inherit" />
              )}
            </IconButton>
          </Tooltip>
        </Box>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
          }}
        >
          <Typography variant="body2" sx={{ mr: 2 }}>
            Tìm kiếm:
          </Typography>
          <TextField
            size="small"
            placeholder="Tìm kiếm nhiệm vụ"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
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

      {/* Filter */}
      <Collapse in={toggleFilter}>
        <Grid container spacing={6} sx={{ px: 4, pb: 2 }}>
          <Grid item sm={1.5} xs={12} xl={1.5} md={1.5}>
            <FormControl fullWidth>
              <InputLabel id="role-category">Thể loại</InputLabel>
              <Select
                fullWidth
                value={filter.categoryId}
                id="select-category"
                label="Thể loại"
                labelId="role-category"
                onChange={(e) =>
                  setFilter({ ...filter, categoryId: e.target.value })
                }
                inputProps={{ placeholder: "Thể loại" }}
              >
                <MenuItem value="">Tất cả</MenuItem>
                {categoryStore.categories.map(
                  ({ categoryId, categoryName }) => (
                    <MenuItem key={categoryId} value={categoryId}>
                      <CustomChip
                        size="medium"
                        skin="light"
                        label={categoryName}
                        color={getCategoryColor(categoryId)}
                      />
                    </MenuItem>
                  )
                )}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={2} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="status-select">Trạng thái</InputLabel>
              <Select
                fullWidth
                value={filter.statusId}
                id="select-status"
                label="Trạng thái"
                labelId="status-select"
                onChange={(e) =>
                  setFilter({ ...filter, statusId: e.target.value })
                }
                inputProps={{ placeholder: "Trạng thái" }}
              >
                <MenuItem value="">Tất cả</MenuItem>
                {statusStore.statuses.map(({ statusId, description }) => (
                  <MenuItem key={statusId} value={statusId}>
                    <CustomChip
                      size="medium"
                      skin="light"
                      label={description}
                      color={getStatusColor(statusId)}
                    />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={2} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="priority-select">Ưu tiên</InputLabel>
              <Select
                fullWidth
                value={filter.priorityId}
                id="select-priority"
                label="Ưu tiên"
                labelId="priority-select"
                onChange={(e) =>
                  setFilter({ ...filter, priorityId: e.target.value })
                }
                inputProps={{ placeholder: "Ưu tiên" }}
              >
                <MenuItem value="">Tất cả</MenuItem>
                {priorityStore.priorities.map(
                  ({ priorityId, priorityName }) => (
                    <MenuItem key={priorityId} value={priorityId}>
                      <CustomChip
                        size="medium"
                        skin="light"
                        label={priorityName}
                        color={getPriorityColor(priorityId)}
                      />
                    </MenuItem>
                  )
                )}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={6} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="asignee-select">Phân công cho</InputLabel>
              <Select
                fullWidth
                value={filter.assigneeId}
                id="select-assignee"
                label="Phân công cho"
                labelId="assignee-select"
                onChange={(e) =>
                  setFilter({ ...filter, assigneeId: e.target.value })
                }
                inputProps={{ placeholder: "Phân công cho" }}
              >
                <MenuItem value="">Tất cả</MenuItem>
                {members.map(({ member }) => (
                  <MenuItem key={member.id} value={member.id}>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <UserAvatar user={member} />
                      <Typography variant="subtitle2">{`${
                        member.firstName ?? ""
                      } ${member.lastName ?? ""}`}</Typography>
                    </Box>
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={2} xs={12}>
            <Box sx={{ display: "flex", gap: 4 }}>
              <Button
                variant="contained"
                sx={{ height: "32px" }}
                onClick={onFilter}
              >
                Apply
              </Button>
              <Button
                variant="contained"
                color="secondary"
                sx={{ height: "32px" }}
                onClick={() => {
                  dispatch(resetFilters());
                  setFilter(filters);
                }}
              >
                Clear
              </Button>
            </Box>
          </Grid>
        </Grid>
      </Collapse>

      {/* Table */}
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
        sx={{
          height: "70vh",
        }}
        localeText={{
          noRowsLabel: "Không có dữ liệu",
        }}
      />

      <DialogAddTask open={openAddTask} setOpen={setOpenAddTask} />
    </Card>
  );
};

export { ProjectViewTasks };
