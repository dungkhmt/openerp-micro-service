import { Typography, Box } from "@mui/material";
import PropTypes from "prop-types";
import { HISTORY_TAGS } from "../utils/constant";

const HistoryStatus = ({ tag }) => {
  return (
    <Box display={"flex"} flexDirection={"row"}>
      <Typography variant={"body1"} sx={{ mr: 1 }}>
        đã
      </Typography>
      <Typography
        variant="body2"
        sx={{
          border: 1,
          borderRadius: "20px",
          borderColor: HISTORY_TAGS[tag].color,
          backgroundColor: HISTORY_TAGS[tag].color,
          px: 2,
          mr: 1,
          color: "#fff",
        }}
      >
        {HISTORY_TAGS[tag].action}
      </Typography>
      <Typography variant={"body1"}>nhiệm vụ</Typography>
    </Box>
  );
};

HistoryStatus.propTypes = {
  tag: PropTypes.number.isRequired,
};

export default HistoryStatus;
