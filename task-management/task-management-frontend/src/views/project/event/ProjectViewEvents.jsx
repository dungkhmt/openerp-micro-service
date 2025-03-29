import {
  Box,
  Button,
  Typography,
  TextField,
  Grid,
  Menu,
  MenuItem,
  IconButton,
  Tooltip,
  useTheme,
} from "@mui/material";
import { Icon } from "@iconify/react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import dayjs from "dayjs";
import { DialogNewEvent } from "./DialogNewEvent";
import { DialogEditEvent } from "./DialogEditEvent";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import { useParams } from "react-router";
import { Link } from "react-router-dom";
import { deleteEvent } from "../../../store/project/events";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import { removeDiacritics } from "../../../utils/stringUtils.js";

const ProjectViewEvents = () => {
  const { id } = useParams();
  const theme = useTheme();
  const { events, fetchLoading } = useSelector((state) => state.events);

  const [createDialog, setCreateDialog] = useState(false);
  const [editDialog, setEditDialog] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [editedEvent, setEditedEvent] = useState(null);
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [eventToDelete, setEventToDelete] = useState(null);

  const [searchQuery, setSearchQuery] = useState("");
  const [filteredEvents, setFilteredEvents] = useState(events);
  const [debouncedSearchQuery, setDebouncedSearchQuery] = useState(searchQuery);
  const [sortOption, setSortOption] = useState("latest");
  const [sortAnchorEl, setSortAnchorEl] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);

  const dispatch = useDispatch();

  const handleSortClick = (event) => {
    setSortAnchorEl(event.currentTarget);
  };

  const handleSortClose = () => {
    setSortAnchorEl(null);
  };

  const handleSortOptionChange = (value) => {
    setSortOption(value);
    handleSortClose();
  };

  const handleCreateEvent = () => {
    setCreateDialog(true);
  };

  const handleClick = (event, e) => {
    setAnchorEl(e.currentTarget);
    setSelectedEvent(event);
  };

  const handleClose = () => {
    setAnchorEl(null);
    setSelectedEvent(null);
  };

  const handleOpenInNewTab = (eventId) => {
    handleClose();
    const eventUrl = `/project/${id}/event/${eventId}`;
    window.open(eventUrl, "_blank", "noopener,noreferrer");
  };

  const handleEdit = () => {
    handleClose();
    setEditedEvent(selectedEvent);
    setEditDialog(true);
  };

  const handleDeleteClick = async (event) => {
    setEventToDelete(event);
    setIsConfirmDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!eventToDelete) return;
    handleClose();
    try {
      await dispatch(
        deleteEvent({
          eventId: eventToDelete.id,
          projectId: id,
        })
      );
      toast.success("Xóa sự kiện thành công.");
    } catch (error) {
      console.error(error);
      toast.error("Lỗi khi xóa sự kiện.");
    } finally {
      setIsConfirmDialogOpen(false);
      setEventToDelete(null);
    }
  };
  const handleCancelDelete = () => {
    setIsConfirmDialogOpen(false);
    setEventToDelete(null);
  };

  useEffect(() => {
    // Debounce search query to optimize performance
    const timeoutId = setTimeout(() => {
      setDebouncedSearchQuery(searchQuery);
    }, 300);
    return () => clearTimeout(timeoutId);
  }, [searchQuery]);

  useEffect(() => {
    let sortedEvents = [...events];

    if (sortOption === "latest") {
      sortedEvents.sort((a, b) => new Date(b.dueDate) - new Date(a.dueDate));
    } else {
      sortedEvents.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
    }

    if (debouncedSearchQuery) {
      sortedEvents = sortedEvents.filter((event) =>
        removeDiacritics(event.name)
          .toLowerCase()
          .includes(debouncedSearchQuery.toLowerCase())
      );
    }

    setFilteredEvents(sortedEvents);
  }, [debouncedSearchQuery, events, sortOption]);

  return (
    <>
      <Box
        sx={{
          position: "sticky",
          top: 0,
          backgroundColor: "background.default",
          zIndex: 10,
          p: 0,
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            height: 50,
            borderBottom: `1px solid ${theme.palette.grey[300]}`,
          }}
        >
          <Box sx={{ flexBasis: "33%", display: "flex", alignItems: "center" }}>
            <TextField
              placeholder="Tìm kiếm sự kiện..."
              variant="outlined"
              fullWidth
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              InputProps={{
                startAdornment: <Icon fontSize={18} icon="ri:search-line" />,
                sx: { height: 40, gap: 2 },
                endAdornment: searchQuery && (
                  <IconButton
                    onClick={() => setSearchQuery("")}
                    sx={{ padding: 0, marginRight: "-4px" }}
                  >
                    <Icon icon="mdi:close" fontSize={20} />
                  </IconButton>
                ),
              }}
            />
          </Box>

          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 5,
            }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                width: { xs: 40, sm: 80 },
                height: 36,
                borderRadius: 15,
                gap: 3,
                border: "1px solid grey",
                cursor: "pointer",
                transition: "background-color 0.3s, border-color 0.3s",
                "&:hover": {
                  backgroundColor: "grey.300",
                  borderColor: "text.primary",
                },
              }}
              onClick={handleSortClick}
            >
              <Icon fontSize={20} icon="fa-solid:sort" sx={{ color: "grey" }} />
              <Typography sx={{ display: { xs: "none", sm: "block" } }}>
                Sort
              </Typography>
            </Box>
            <Menu
              anchorEl={sortAnchorEl}
              open={Boolean(sortAnchorEl)}
              onClose={handleSortClose}
              slotProps={{
                paper: {
                  style: {
                    minWidth: "8rem",
                    marginTop: "8px",
                    padding: "3px 10px",
                    boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.4)",
                    borderRadius: "8px",
                    border: `1px solid ${theme.palette.grey[300]}`,
                  },
                },
              }}
            >
              <MenuItem onClick={() => handleSortOptionChange("latest")}>
                Most recent
              </MenuItem>
              <MenuItem onClick={() => handleSortOptionChange("oldest")}>
                Oldest first
              </MenuItem>
            </Menu>
            <Button
              onClick={handleCreateEvent}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none" }}
            >
              <Icon
                icon="fluent:add-16-filled"
                fontSize={20}
                sx={{ display: { xs: "block", sm: "none" } }}
              />
              <Box
                component="span"
                sx={{ display: { xs: "none", sm: "block" } }}
              >
                Thêm Sự Kiện
              </Box>
            </Button>
          </Box>
        </Box>
      </Box>

      <Box
        sx={{
          display: "flex",
          alignItems: "center",
        }}
      >
        {fetchLoading ? (
          <CircularProgressLoading />
        ) : filteredEvents.length ? (
          <Grid container spacing={2} sx={{ mt: 0 }}>
            {filteredEvents.map((event) => (
              <Grid item xs={12} sm={6} md={4} key={event.id}>
                <Box
                  sx={{
                    border: "1px solid",
                    borderColor: "rgba(0, 0, 0, 0.08)",
                    borderRadius: "15px",
                    padding: 5,
                    backgroundColor: "white",
                  }}
                >
                  <Box
                    sx={{
                      display: "flex",
                      justifyContent: "space-between",
                      mb: 2,
                      alignItems: "center",
                    }}
                  >
                    <Tooltip title={event.name} placement="top-start">
                      <Typography
                        variant="h6"
                        component={Link}
                        to={`/project/${id}/event/${event.id}`}
                        sx={{
                          color: "grey.800",
                          flexGrow: 1,
                          overflow: "hidden",
                          whiteSpace: "nowrap",
                          textOverflow: "ellipsis",
                          mr: 1,
                          textDecoration: "none",
                          textTransform: "capitalize",
                        }}
                      >
                        {event.name}
                      </Typography>
                    </Tooltip>
                    <Typography
                      variant="subtitle1"
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        fontSize: "0.85rem",
                        backgroundColor: "grey.150",
                        borderRadius: "20px",
                        padding: "2px 10px",
                        minWidth: "100px",
                        color: "grey.800",
                      }}
                    >
                      {dayjs(event.dueDate).format("DD MMM, YYYY")}
                    </Typography>
                  </Box>

                  <Box
                    sx={{
                      mb: 2,
                      height: "14vh",
                      overflowY: event.description ? "auto" : "hidden",
                      overflowX: "hidden",
                      "&::-webkit-scrollbar": { display: "none" },
                      scrollbarWidth: "none",
                      msOverflowStyle: "none",
                      p: 1,
                      wordBreak: "break-word",
                      whiteSpace: "pre-wrap",
                    }}
                  >
                    <Typography
                      variant="body1"
                      gutterBottom
                      sx={{ fontSize: "0.95rem", color: "text.primary" }}
                    >
                      {event.description ? (
                        event.description.split("\n").map((line, index) => (
                          <Box key={index} component="span">
                            {line}
                            <br />
                          </Box>
                        ))
                      ) : (
                        <Box
                          sx={{
                            backgroundColor: "grey.100",
                            borderRadius: "10px",
                            height: "13vh",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                          }}
                        >
                          <Typography
                            variant="body2"
                            sx={{ color: "text.secondary" }}
                          >
                            Không có mô tả.
                          </Typography>
                        </Box>
                      )}
                    </Typography>
                  </Box>

                  <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
                    {event.eventUsers && event.eventUsers.length > 0 ? (
                      <GroupedAvatars
                        users={event.eventUsers}
                        max_displayed_users={5}
                      />
                    ) : (
                      <Typography
                        variant="body2"
                        sx={{
                          color: "text.secondary",
                          height: 30,
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        Không có thành viên
                      </Typography>
                    )}
                  </Box>

                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "space-between",
                    }}
                  >
                    <Box
                      sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 2,
                        color: "text.secondary",
                      }}
                    >
                      <Icon fontSize={16} icon="nrk:media-media-complete" />
                      {event.totalTasks > 0 ? (
                        <Typography
                          sx={{ fontSize: "0.875rem", color: "text.primary" }}
                        >
                          {`${event.finishedTasks}/${event.totalTasks} nhiệm vụ`}
                        </Typography>
                      ) : (
                        <Typography
                          sx={{ fontSize: "0.875rem", color: "text.secondary" }}
                        >
                          Không có nhiệm vụ
                        </Typography>
                      )}
                    </Box>
                    <IconButton
                      onClick={(e) => handleClick(event, e)}
                      sx={{
                        color: "text.primary",
                        transition: "background-color 0.5s",
                        "&:hover": {
                          backgroundColor: "grey.300",
                          "& svg": { color: "text.primary" },
                        },
                      }}
                    >
                      <Icon fontSize={18} icon="tabler:dots" />
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
                            boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)",
                            borderRadius: "8px",
                          },
                        },
                      }}
                    >
                      <MenuItem
                        onClick={() => handleOpenInNewTab(selectedEvent.id)}
                        sx={{ gap: 3 }}
                      >
                        <Icon fontSize={18} icon="ic:round-open-in-new" />
                        <Typography variant="body1">
                          Mở trong tab mới
                        </Typography>
                      </MenuItem>
                      <MenuItem onClick={handleEdit} sx={{ gap: 3 }}>
                        <Icon fontSize={18} icon="mingcute:pencil-line" />
                        <Typography variant="body1">Chỉnh sửa</Typography>
                      </MenuItem>
                      <MenuItem
                        onClick={() => handleDeleteClick(selectedEvent)}
                        sx={{ gap: 3 }}
                      >
                        <Icon fontSize={18} icon="mingcute:delete-2-line" />
                        <Typography variant="body1">Xóa</Typography>
                      </MenuItem>
                    </Menu>
                  </Box>
                </Box>
              </Grid>
            ))}
          </Grid>
        ) : (
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              textAlign: "center",
              width: "100%",
            }}
          >
            <Typography variant="h6" sx={{ pt: 5, mb: 2 }}>
              Không có sự kiện nào
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
              Hãy thêm sự kiện để bắt đầu quản lý chúng!
            </Typography>
            <Button
              onClick={handleCreateEvent}
              color="primary"
              variant="contained"
              sx={{ textTransform: "none", mb: 5 }}
              startIcon={<Icon icon="fluent:add-16-filled" />}
            >
              Thêm Sự Kiện
            </Button>
          </Box>
        )}

        <DialogNewEvent
          openDialog={createDialog}
          setOpenDialog={setCreateDialog}
        />
        {editedEvent && (
          <DialogEditEvent
            event={editedEvent}
            openDialog={editDialog}
            setOpenDialog={setEditDialog}
          />
        )}
        {eventToDelete && (
          <ConfirmationDialog
            open={isConfirmDialogOpen}
            onClose={handleCancelDelete}
            onConfirm={handleConfirmDelete}
            title="Xoá sự kiện"
            content={
              <>
                Bạn có chắc chắn muốn xoá{" "}
                <span style={{ fontWeight: "bold" }}>{eventToDelete.name}</span>
                ? Hành động này không thể hoàn tác.
              </>
            }
          />
        )}
      </Box>
    </>
  );
};

export { ProjectViewEvents };
