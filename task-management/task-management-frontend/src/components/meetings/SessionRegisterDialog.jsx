import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  List,
  ListItem,
  IconButton,
  Card,
  Box,
  Typography,
} from "@mui/material";
import { Icon } from "@iconify/react";
import dayjs from "dayjs";

const SessionRegisterDialog = ({
  open,
  onClose,
  selectedSessions,
  sessions,
  onToggle,
  onSave,
  loading,
}) => {
  const isAllSelected =
    sessions.length > 0 && selectedSessions.length === sessions.length;

  const handleSelectAll = () => {
    if (isAllSelected) {
      // Deselect all: toggle off each selected session individually
      sessions.forEach((session) => {
        if (selectedSessions.includes(session.id)) {
          onToggle(session.id);
        }
      });
    } else {
      // Select all: toggle on each session that is not already selected
      sessions.forEach((session) => {
        if (!selectedSessions.includes(session.id)) {
          onToggle(session.id);
        }
      });
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle sx={{ display: "flex", justifyContent: "space-between" }}>
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          Đăng Ký Phiên Họp
          <Typography
            variant="body2"
            sx={{ fontWeight: 600, color: "text.secondary" }}
          >
            ({selectedSessions.length}/{sessions.length})
          </Typography>
        </Box>
        <IconButton onClick={onClose}>
          <Icon icon="mdi:close" fontSize={18} />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        {sessions.length > 0 && (
          <Box
            sx={{
              position: "sticky",
              top: 0,
              backgroundColor: "white",
              zIndex: 1,
              py: 2,
              borderTop: 1,
              borderBottom: 1,
              borderColor: "divider",
              display: "flex",
              alignItems: "center",
              gap: 2,
              pl: 1,
            }}
          >
            <Icon
              onClick={handleSelectAll}
              icon={
                isAllSelected
                  ? "ci:checkbox-check"
                  : "fluent:checkbox-unchecked-16-filled"
              }
              fontSize={24}
            />
            <Typography
              varient="body1"
              color="text.primary"
              sx={{ fontWeight: 600 }}
            >
              Chọn tất cả
            </Typography>
          </Box>
        )}
        <List>
          {sessions.length > 0 ? (
            sessions.map((session) => {
              const isSelected = selectedSessions.includes(session.id);
              return (
                <Card
                  key={session.id}
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    gap: 2,
                    mb: 1.5,
                    borderRadius: 2,
                    p: 1,
                    cursor: "pointer",
                    backgroundColor: isSelected
                      ? "primary.background"
                      : "transparent",
                  }}
                  onClick={() => onToggle(session.id)}
                >
                  <Icon
                    icon={
                      isSelected
                        ? "ci:checkbox-check"
                        : "fluent:checkbox-unchecked-16-filled"
                    }
                    fontSize={24}
                  />
                  <Box>
                    <Typography
                      variant="body2"
                      color="text.primary"
                      fontWeight={600}
                    >
                      {dayjs(session.startTime).format("HH:mm A")} -{" "}
                      {dayjs(session.endTime).format("HH:mm A")}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {dayjs(session.startTime).format("dddd, DD MMM YYYY")}
                    </Typography>
                  </Box>
                </Card>
              );
            })
          ) : (
            <ListItem sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <Icon icon="mdi:alert-circle-outline" color="red" />
              <Typography>Không có phiên họp nào</Typography>
            </ListItem>
          )}
        </List>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Hủy</Button>
        <Button onClick={onSave} variant="contained" disabled={loading}>
          {loading ? "Đang lưu..." : "Lưu"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

SessionRegisterDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  selectedSessions: PropTypes.arrayOf(PropTypes.string).isRequired,
  sessions: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      startTime: PropTypes.string.isRequired,
      endTime: PropTypes.string.isRequired,
    })
  ).isRequired,
  onToggle: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
  loading: PropTypes.bool.isRequired,
};

export default SessionRegisterDialog;
