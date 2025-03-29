import { Box, Typography, IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import dayjs from "dayjs";
import PropTypes from "prop-types";

const SessionCard = ({ session, sx = {}, handleDeleteClick, isEditable }) => {
  return (
    <Box
      sx={{
        mb: 2,
        p: 2,
        borderRadius: 2,
        bgcolor: "grey.100",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: 1,
        ...sx,
      }}
    >
      <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
        {/* Time Slot */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Icon icon="mingcute:time-line" />
          <Typography
            variant="body2"
            sx={{ fontWeight: 600, color: "text.primary" }}
          >
            {dayjs(session.startTime).format("HH:mm A")} -{" "}
            {dayjs(session.endTime).format("HH:mm A")}
          </Typography>
        </Box>
        {/* Date */}
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Icon icon="lets-icons:date-today" />
          <Typography variant="caption" color="text.secondary">
            {dayjs(session.startTime).format("dddd, DD MMM YYYY")}
          </Typography>
        </Box>
      </Box>
      {isEditable && (
        <IconButton
          onClick={() => handleDeleteClick(session)}
          sx={{
            color: "secondary.light",
            "&:hover": {
              color: "secondary.dark",
            },
          }}
        >
          <Icon icon="mdi:delete" fontSize={20} />
        </IconButton>
      )}
    </Box>
  );
};

SessionCard.propTypes = {
  session: PropTypes.shape({
    startTime: PropTypes.string.isRequired,
    endTime: PropTypes.string.isRequired,
  }).isRequired,
  sx: PropTypes.object,
  handleDeleteClick: PropTypes.func,
  isEditable: PropTypes.bool,
};

export default SessionCard;
