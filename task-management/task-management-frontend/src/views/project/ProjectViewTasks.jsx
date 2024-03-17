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
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import CustomChip from "../../components/mui/chip";
import { useDebounce } from "../../hooks/useDebounce";
import { useProjectContext } from "../../hooks/useProjectContext";
import { TaskService } from "../../services/api/task.service";
import {
  getCategoryColor,
  getDueDateColor,
  getPriorityColor,
  getProgressColor,
  getRandomColorSkin,
  getStatusColor,
} from "../../utils/color.util";
import { DialogAddTask } from "./DialogAddTask";

const initFilter = {
  categoryId: "",
  statusId: "",
  priorityId: "",
  assignee: "",
};

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const DEFAULT_SORT = {
  sort: "desc",
  column: "createdStamp",
};

const ProjectViewTasks = () => {
  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );
  const [search, setSearch] = useState("");
  const [toggleFilter, setToggleFilter] = useState(false);
  const [filter, setFilter] = useState(initFilter);
  const [sort, setSort] = useState(DEFAULT_SORT);
  const [rows, setRows] = useState([]);
  const searchDebounce = useDebounce(search, 1000);
  const { project, statuses, categories, priorities, members } =
    useProjectContext();
  const [totalRows, setTotalRows] = useState(project.taskCount);
  const [isLoading, setIsLoading] = useState(false);

  const [openAddTask, setOpenAddTask] = useState(false);

  const columns = [
    {
      flex: 0.3,
      field: "name",
      headerName: "Tên",
      filterable: false,
      renderCell: ({ row }) => {
        const category = categories.find(
          (category) => category.categoryId === row.categoryId
        );
        const priority = priorities.find(
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
        const status = statuses.find(
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
              <CustomAvatar
                skin="light"
                color={getRandomColorSkin(row.assignee?.id)}
                sx={{ width: 30, height: 30, fontSize: ".875rem" }}
              >
                {`${row.assignee?.firstName?.charAt(0) ?? ""}${
                  row.assignee?.lastName?.charAt(0) ?? ""
                }`}
              </CustomAvatar>
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
      filterable: false,
      renderCell: ({ row }) => (
        <Typography
          variant="body2"
          sx={{ color: getDueDateColor(row.dueDate) }}
        >
          {dayjs(row.dueDate).format("DD/MM/YYYY") ?? ""}
        </Typography>
      ),
    },
  ];

  const handleSortModel = (newModel) => {
    if (newModel.length) {
      const { sort, field } = newModel[0];
      setSort({ sort, column: field });
    } else {
      setSort(DEFAULT_SORT);
    }
  };

  const handlePaginationModel = (newModel) => {
    setPaginationModel(newModel);
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

  const getTasksPagination = async () => {
    setIsLoading(true);
    try {
      const taskPagination = await TaskService.getTasks(project.id, {
        page: paginationModel.page,
        size: paginationModel.pageSize,
        q: buildQueryString(),
        sort: `${sort.column},${sort.sort}`,
      });
      const { data, totalElements } = taskPagination;
      setRows(data?.map((data, index) => ({ ...data, _id: index })) ?? []);
      setTotalRows(totalElements ?? 0);
    } catch (error) {
      console.log(error);
      toast.error("Có lỗi khi lấy danh sách nhiệm vụ");
    } finally {
      setIsLoading(false);
    }
  };

  const onFilter = async () => {
    setPaginationModel(DEFAULT_PAGINATION_MODEL);
    if (paginationModel === DEFAULT_PAGINATION_MODEL) {
      getTasksPagination();
    }
  };

  useEffect(() => {
    getTasksPagination();
  }, [searchDebounce, paginationModel, project.id, sort]);

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
                {categories.map(({ categoryId, categoryName }) => (
                  <MenuItem key={categoryId} value={categoryId}>
                    <CustomChip
                      size="medium"
                      skin="light"
                      label={categoryName}
                      color={getCategoryColor(categoryId)}
                    />
                  </MenuItem>
                ))}
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
                {statuses.map(({ statusId, description }) => (
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
                {priorities.map(({ priorityId, priorityName }) => (
                  <MenuItem key={priorityId} value={priorityId}>
                    <CustomChip
                      size="medium"
                      skin="light"
                      label={priorityName}
                      color={getPriorityColor(priorityId)}
                    />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={6} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="asignee-select">Phân công cho</InputLabel>
              <Select
                fullWidth
                value={filter.assignee}
                id="select-assignee"
                label="Phân công cho"
                labelId="assignee-select"
                onChange={(e) =>
                  setFilter({ ...filter, assignee: e.target.value })
                }
                inputProps={{ placeholder: "Phân công cho" }}
              >
                <MenuItem value="">Tất cả</MenuItem>
                {members.map(({ member }) => (
                  <MenuItem key={member.id} value={member.id}>
                    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                      <CustomAvatar
                        skin="light"
                        color={getRandomColorSkin(member.id)}
                        sx={{
                          width: 30,
                          height: 30,
                          fontSize: ".875rem",
                        }}
                      >
                        {`${member?.firstName?.charAt(0) ?? ""}${
                          member?.lastName?.charAt(0) ?? ""
                        }`}
                      </CustomAvatar>
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
                  setFilter(initFilter);
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
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationModel}
        pagination
        paginationMode="server"
        rowCount={totalRows}
        loading={isLoading}
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
