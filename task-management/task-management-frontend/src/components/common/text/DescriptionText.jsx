import { Box, Typography } from "@mui/material";
import PropTypes from "prop-types";

const DescriptionText = ({ text, noDataText = "Không có mô tả", sx = {} }) => {
  if (!text) {
    return (
      <Typography variant="body2" sx={{ mb: 2, fontStyle: "italic", ...sx }}>
        {noDataText}
      </Typography>
    );
  }

  return (
    <Typography
      variant="body1"
      sx={{
        mb: 2,
        overflowX: "hidden",
        wordBreak: "break-word",
        whiteSpace: "pre-wrap",
        ...sx,
      }}
    >
      {text.split("\n").map((line, index) => (
        <Box key={index} component="span">
          {line}
          <br />
        </Box>
      ))}
    </Typography>
  );
};

DescriptionText.propTypes = {
  text: PropTypes.string.isRequired,
  noDataText: PropTypes.string,
  sx: PropTypes.object,
};

export default DescriptionText;
