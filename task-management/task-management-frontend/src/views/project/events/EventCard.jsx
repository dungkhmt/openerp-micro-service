import {
  Box,
  IconButton,
  Menu,
  MenuItem,
  Tooltip,
  Typography,
} from "@mui/material";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import PropTypes from "prop-types";
import { useParams } from "react-router";
import { useState } from "react";
import { Link } from "react-router-dom";
import dayjs from "dayjs";
import { Icon } from "@iconify/react";
import { deleteEvent } from "../../../store/project/events";
import toast from "react-hot-toast";
import { DialogEditEvent } from "./DialogEditEvent";
import ConfirmationDialog from "../../../components/mui/dialog/ConfirmationDialog";
import { useDispatch } from "react-redux";

const EventCard = ({ event }) => {
  const { id: projectId } = useParams();
  const dispatch = useDispatch();
  const [anchorEl, setAnchorEl] = useState(null);
  const [editDialog, setEditDialog] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [editedEvent, setEditedEvent] = useState(null);
  const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
  const [eventToDelete, setEventToDelete] = useState(null);

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
    const eventUrl = `/project/${projectId}/events/${eventId}`;
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
          projectId: projectId,
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

  return (
    <>
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
              to={`/project/${projectId}/events/${event.id}`}
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
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 1,
            }}
          >
            <Tooltip
              title={`Bắt đầu: ${
                event.startDate
                  ? dayjs(event.startDate).format("DD MMM, YYYY")
                  : "Chưa xác định"
              } | Kết thúc: ${
                event.dueDate
                  ? dayjs(event.dueDate).format("DD MMM, YYYY")
                  : "Chưa xác định"
              }`}
              placement="top"
            >
              <Typography
                variant="subtitle2"
                sx={{
                  fontSize: "0.8rem",
                  backgroundColor: "grey.150",
                  borderRadius: "16px",
                  padding: "2px 8px",
                  color: "grey.800",
                  whiteSpace: "nowrap",
                }}
              >
                {event.startDate
                  ? dayjs(event.startDate).format("DD MMM")
                  : "_"}{" "}
                -{" "}
                {event.dueDate
                  ? dayjs(event.dueDate).format("DD MMM, YYYY")
                  : "_"}
              </Typography>
            </Tooltip>
          </Box>
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
                <Typography variant="body2" sx={{ color: "text.secondary" }}>
                  Không có mô tả.
                </Typography>
              </Box>
            )}
          </Typography>
        </Box>

        <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
          {event.eventUsers && event.eventUsers.length > 0 ? (
            <GroupedAvatars users={event.eventUsers} max_displayed_users={5} />
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
              <Typography sx={{ fontSize: "0.875rem", color: "text.primary" }}>
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
            anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            transformOrigin={{ vertical: "top", horizontal: "right" }}
            slotProps={{
              paper: {
                style: {
                  minWidth: "10rem",
                  marginTop: "8px",
                  padding: "3px 10px",
                  boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.4)",
                  borderRadius: "8px",
                },
              },
            }}
          >
            <MenuItem
              onClick={() => handleOpenInNewTab(event.id)}
              sx={{ gap: 3 }}
            >
              <Icon fontSize={18} icon="ic:round-open-in-new" />
              <Typography variant="body1">Mở trong tab mới</Typography>
            </MenuItem>
            <MenuItem onClick={() => handleEdit(event)} sx={{ gap: 3 }}>
              <Icon fontSize={18} icon="mingcute:pencil-line" />
              <Typography variant="body1">Chỉnh sửa</Typography>
            </MenuItem>
            <MenuItem onClick={() => handleDeleteClick(event)} sx={{ gap: 3 }}>
              <Icon fontSize={18} icon="mingcute:delete-2-line" />
              <Typography variant="body1">Xóa</Typography>
            </MenuItem>
          </Menu>
        </Box>
      </Box>

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
              <span style={{ fontWeight: "bold" }}>{eventToDelete.name}</span>?
              Hành động này không thể hoàn tác.
            </>
          }
        />
      )}
    </>
  );
};

EventCard.propTypes = {
  event: PropTypes.object.isRequired,
};

export default EventCard;
