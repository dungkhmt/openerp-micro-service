import { Box, Typography } from "@mui/material";
import PropTypes from "prop-types";

const CountBadge = ({ count }) => {
  return (
    <Box
      sx={{
        backgroundColor: "primary.background",
        px: 1.5,
        borderRadius: 3,
      }}
    >
      <Typography
        sx={{
          fontSize: "0.8rem",
          fontWeight: 600,
          color: "primary.main",
        }}
      >
        {count}
      </Typography>
    </Box>
  );
};

CountBadge.propTypes = {
  count: PropTypes.number.isRequired,
};

export default CountBadge;
