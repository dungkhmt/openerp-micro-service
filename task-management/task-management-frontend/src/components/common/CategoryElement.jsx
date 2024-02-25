import PropTypes from "prop-types";
import { Typography } from "@mui/material";

import { TASK_CATEGORY_COLOR } from "../utils/constant";

const CategoryElement = ({ categoryId, value }) => {
  return (
    <Typography
      variant="body2"
      sx={{
        border: 1,
        borderRadius: "20px",
        borderColor: TASK_CATEGORY_COLOR[categoryId],
        backgroundColor: TASK_CATEGORY_COLOR[categoryId],
        px: 2,
        color: "#fff",
        width: "fit-content",
        textAlign: "center",
      }}
    >
      {value}
    </Typography>
  );
};

CategoryElement.propTypes = {
  categoryId: PropTypes.number.isRequired,
  value: PropTypes.string.isRequired,
};

export default CategoryElement;
