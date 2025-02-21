/* eslint-disable react-hooks/exhaustive-deps */
import {
  Box,
  Typography,
  Card,
  IconButton,
  Grid,
  Menu,
  MenuItem,
  Tooltip,
  LinearProgress,
  AvatarGroup,
} from "@mui/material";
import { Icon } from "@iconify/react";
import { Link } from "react-router-dom";
import { DataGrid } from "@mui/x-data-grid";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import { useSelector } from "react-redux";
import { Helmet } from "react-helmet";
import { useEffect, useState, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { EventService } from "../../../../../services/api/event.service";
import { TaskService } from "../../../../../services/api/task.service";
import { CircularProgressLoading } from "../../../../../components/common/loading/CircularProgressLoading";
import { DialogAddTask } from "../../../../../views/project/tasks/DialogAddTask";
import { DialogAddExistingTask } from "../../../../../views/project/event/DialogAddExistingTask";
import { TaskPriority } from "../../../../../components/task/priority";
import { TaskCategory } from "../../../../../components/task/category";
import { TaskStatus } from "../../../../../components/task/status";
import { UserAvatar } from "../../../../../components/common/avatar/UserAvatar";
import {
  getDueDateColor,
  getProgressColor,
} from "../../../../../utils/color.util";
import { deleteEvent } from "../../../../../store/project/events";
import { useDispatch } from "react-redux";
import { DialogEditEvent } from "../../../../../views/project/event/DialogEditEvent";
import ConfirmationDialog from "../../../../../components/mui/dialog/ConfirmationDialog";


const Event = () => {
  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);

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
      flex: 0.1,
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

  const navigate = useNavigate();
  const { id, eid } = useParams();
  const dispatch = useDispatch();

  const [event, setEvent] = useState(null);
  const [eventLoading, setEventLoading] = useState(true);

  const [eventTasks, setEventTasks] = useState([]);
  const [eventTasksLoading, setEventTasksLoading] = useState(true);

  const [eventUsers, setEventUsers] = useState([]);
  const [eventUsersLoading, setEventUsersLoading] = useState(true);

  const [error, setError] = useState(null);
  const [isUpdate, setIsUpdate] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [addNewTaskDialog, setAddNewTaskDialog] = useState(false);
  const [addExistingTaskDialog, setAddExistingTaskDialog] = useState(false);
  const [editDialog, setEditDialog] = useState(false);
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);

  const { project, fetchLoading: projectLoading } = useSelector(
    (state) => state.project
  );

  const handleClick = (e) => {
    setAnchorEl(e.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleAddExistingClick = () => {
    handleClose();
    setAddExistingTaskDialog(true);
  };

  const handleAddNewTaskClick = () => {
    handleClose();
    setAddNewTaskDialog(true);
  };

  const handleDeleteClick = () => {
    setIsConfirmDialogOpen(true);
  }

  const handleCancelDelete = () => {
    setIsConfirmDialogOpen(false);
  };

  const getEvent = useCallback(async () => {
    try {
      setEventLoading(true);
      const res = await EventService.getEvent(eid);
      setEvent(res);
    } catch (error) {
      console.error(error);
      setError(error);
      toast.error("Lỗi khi lấy dữ liệu sự kiện.");
    } finally {
      setEventLoading(false);
    }
  }, [eid, isUpdate]);

  const getEventUsers = useCallback(async () => {
    try {
      setEventUsersLoading(true);
      const res = await EventService.getEventUsers(eid);
      setEventUsers(res);
    } catch (error) {
      console.error(error);
      setError(error);
      toast.error("Lỗi khi lấy dữ liệu thành viên tham gia sự kiện.");
    } finally {
      setEventUsersLoading(false);
    }
  }, [eid, isUpdate]);

  const getEventTasks = useCallback(async () => {
    try {
      setEventTasksLoading(true);
      const res = await TaskService.getEventTasks(eid);
      setEventTasks(res);
    } catch (error) {
      console.error(error);
      setError(error);
      toast.error("Lỗi khi lấy dữ liệu nhiệm vụ của sự kiện.");
    } finally {
      setEventTasksLoading(false);
    }
  }, [eid, isUpdate]);

  const handleEdit = () => {
    setEditDialog(true);
  };

  const handleConfirmDelete = async () => {
    try {
      await dispatch(
        deleteEvent({
          eventId: eid,
          projectId: id,
        })
      );
      toast.success("Xóa sự kiện thành công.");
    } catch (error) {
      console.error(error);
      toast.error("Lỗi khi xóa sự kiện.");
    } finally {
      setIsConfirmDialogOpen(false);
      navigate(`/project/${id}/event`);
    }
  };

  useEffect(() => {
    getEvent();
    getEventUsers();
    getEventTasks();
  }, [getEvent, getEventTasks, getEventUsers]);

  if (error) {
    if (error.response?.status === 404) return <h1>Không tìm thấy sự kiện</h1>;
    else {
      toast.error("Có lỗi xảy ra khi tải dữ liệu");
      return null;
    }
  }

  if (
    projectLoading ||
    eventLoading ||
    eventUsersLoading ||
    eventTasksLoading
  ) {
    return <CircularProgressLoading />;
  }

  return (
    <>
      <Helmet>
        <title>
          {`${
            event?.name?.length > 50
              ? `${event.name.slice(0, 50)}...`
              : event?.name ?? ""
          } | ${project?.name ?? ""} | Task management`}
        </title>
      </Helmet>
      <IconButton onClick={() => navigate(-1)}>
        <Icon fontSize={24} icon="mdi:arrow-left" />
      </IconButton>

      <Box sx={{ pr: 2, pl: 1, overflow: "auto", maxHeight: "80vh" }}>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            mb: 2,
            mt: 2,
          }}
        >
          <Typography
            variant="h4"
            sx={{
              wordBreak: "break-word",
              whiteSpace: "pre-wrap",
              textTransform: "capitalize",
            }}
            gutterBottom
          >
            {event.name}
          </Typography>
          <Box sx={{ minWidth: 100, ml: 10 }}>
            <IconButton
              onClick={handleEdit}
              sx={{
                transition: "background-color 0.5s",
                border: "1px solid #B0E0E6",
                borderRadius: "4px",
                mr: 3,
                color: "#0000CD",
                "&:hover": {
                  backgroundColor: "#B0E0E6",
                },
              }}
            >
              <Icon fontSize={18} icon="mingcute:pencil-line" />
            </IconButton>
            <IconButton
              onClick={handleDeleteClick}
              sx={{
                transition: "background-color 0.5s",
                border: "1px solid #FFCCCC",
                color: "#FF0000",
                borderRadius: "4px",
                mr: 3,
                "&:hover": {
                  backgroundColor: "#FFCCCC",
                },
              }}
            >
              <Icon fontSize={18} icon="mingcute:delete-2-line" />
            </IconButton>
          </Box>
        </Box>

        <Grid container spacing={3}>
          <Grid item xs={8.5}>
            <Card sx={{ p: 5, borderRadius: 3, mb: 3 }}>
              <Typography variant="h6" gutterBottom>
                Mô tả
              </Typography>
              <Typography
                variant="body1"
                sx={{
                  overflowX: "hidden",
                  wordBreak: "break-word",
                  whiteSpace: "pre-wrap",
                }}
              >
                {event.description ? (
                  event.description.split("\n").map((line, index) => (
                    <Box key={index} component="span">
                      {line}
                      <br />
                    </Box>
                  ))
                ) : (
                  <Typography variant="body1" color="textSecondary">
                    Không có mô tả.
                  </Typography>
                )}
              </Typography>
            </Card>
            <Card sx={{ p: 5, borderRadius: 3 }}>
              <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                <Typography variant="h6" gutterBottom>
                  Nhiệm vụ
                </Typography>
                <IconButton
                  variant="outlined"
                  onClick={handleClick}
                  sx={{
                    ml: "auto",
                    mr: 2,
                    transition: "background-color 0.5s",
                    "&:hover": {
                      backgroundColor: "lightgrey",
                      "& svg": { color: "black" },
                    },
                  }}
                >
                  <Icon fontSize={18} icon="fluent:add-12-filled" />
                </IconButton>
                <Menu
                  anchorEl={anchorEl}
                  open={Boolean(anchorEl)}
                  onClose={handleClose}
                  anchorOrigin={{
                    vertical: "bottom",
                    horizontal: "right",
                  }}
                  transformOrigin={{
                    vertical: "top",
                    horizontal: "right",
                  }}
                  slotProps={{
                    paper: {
                      style: {
                        minWidth: "10rem",
                        marginTop: "8px",
                        padding: "3px 10px",
                        boxShadow: "1px 2px 4px rgba(0, 0, 0, 0.4)",
                        borderRadius: "8px",
                      },
                    },
                  }}
                >
                  <MenuItem onClick={handleAddNewTaskClick} sx={{ gap: 3 }}>
                    <Icon fontSize={18} icon="icon-park-outline:add" />
                    <Typography variant="body1">Tạo nhiệm vụ mới</Typography>
                  </MenuItem>
                  <MenuItem onClick={handleAddExistingClick} sx={{ gap: 3 }}>
                    <Icon fontSize={18} icon="fluent:task-list-add-24-filled" />
                    <Typography variant="body1">
                      Thêm nhiệm vụ hiện có
                    </Typography>
                  </MenuItem>
                </Menu>
              </Box>
              <Box sx={{ width: "100%" }}>
                {eventTasks.length > 0 ? (
                  <DataGrid
                    rows={eventTasks}
                    columns={columns}
                    pageSize={5}
                    rowsPerPageOptions={[5]}
                  />
                ) : (
                  <Typography
                    variant="body1"
                    color="textSecondary"
                    sx={{ mt: 2 }}
                  >
                    Không có nhiệm vụ nào.
                  </Typography>
                )}
              </Box>
            </Card>
          </Grid>

          <Grid item xs={3.5}>
            <Card sx={{ p: 5, borderRadius: 3, mb: 3 }}>
              <Typography variant="h6" gutterBottom>
                Ngày
              </Typography>
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  gap: 2,
                }}
              >
                <Icon fontSize={20} icon="lets-icons:date-today" />
                <Typography
                  variant="subtitle1"
                  sx={{
                    fontSize: "0.9rem",
                  }}
                >
                  {dayjs(event.dueDate).format("DD MMMM, YYYY")}
                </Typography>
              </Box>
            </Card>
            <Card sx={{ p: 5, borderRadius: 3 }}>
              <Typography variant="h6" gutterBottom>
                Thành viên
              </Typography>
              <Box
                sx={{
                  overflowX: "auto",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                {eventUsers && eventUsers.length > 0 ? (
                  eventUsers.map((row, index) => {
                    const { firstName, lastName, email } = row;
                    const fullName =
                      firstName || lastName
                        ? `${firstName ?? ""} ${lastName ?? ""}`
                        : " - ";
                    return (
                      <Box
                        key={index}
                        sx={{
                          display: "flex",
                          alignItems: "center",
                          gap: 2,
                          mb: 2,
                        }}
                      >
                        <UserAvatar user={row} />
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "flex-start",
                            flexDirection: "column",
                          }}
                        >
                          <Tooltip title={fullName}>
                            <Typography
                              onClick={() => setSelectedUser(row)}
                              sx={{
                                fontWeight: 600,
                                fontSize: "0.9rem",
                                color: "#696969",
                                "&:hover": {
                                  cursor: "pointer",
                                },
                              }}
                            >
                              {fullName}
                            </Typography>
                          </Tooltip>
                          <Typography noWrap variant="caption">
                            {`${email ? email : ""}`}
                          </Typography>
                        </Box>
                      </Box>
                    );
                  })
                ) : (
                  <Typography variant="body2" color="textSecondary">
                    Không có thành viên.
                  </Typography>
                )}
              </Box>
            </Card>
          </Grid>
        </Grid>

        <DialogAddTask
          open={addNewTaskDialog}
          setOpen={setAddNewTaskDialog}
          defaultEvent={event}
        />
        <DialogAddExistingTask
          open={addExistingTaskDialog}
          setOpen={setAddExistingTaskDialog}
          isUpdate={isUpdate}
          setIsUpdate={setIsUpdate}
        />
        <DialogEditEvent
          event={{ ...event, eventUsers }}
          openDialog={editDialog}
          setOpenDialog={setEditDialog}
          isUpdate={isUpdate}
          setIsUpdate={setIsUpdate}
        />
        <ConfirmationDialog
          open={isConfirmDialogOpen}
          onClose={handleCancelDelete}
          onConfirm={handleConfirmDelete}
          title="Xoá sự kiện"
          content={
            <>
              Bạn có chắc chắn muốn xoá{" "}
              <span style={{ fontWeight: "bold" }}>{event.name}</span>?
              Hành động này không thể hoàn tác.
            </>
          }
        />
      </Box>
    </>
  );
};

export default Event;
