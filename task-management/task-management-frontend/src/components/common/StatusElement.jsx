import PropTypes from "prop-types";
import { Typography } from "@mui/material";

import { TASK_STATUS_COLOR } from "../utils/constant";

const StatusElement = ({ statusId, value }) => {
  return (
    <Typography
      variant="body2"
      sx={{
        border: 1,
        borderRadius: "20px",
        borderColor: TASK_STATUS_COLOR[statusId],
        backgroundColor: TASK_STATUS_COLOR[statusId],
        px: 2,
        mr: 2,
        color: "#fff",
      }}
    >
      {value}
    </Typography>
  );
};

StatusElement.propTypes = {
  statusId: PropTypes.number.isRequired,
  value: PropTypes.string.isRequired,
};

export default StatusElement;
