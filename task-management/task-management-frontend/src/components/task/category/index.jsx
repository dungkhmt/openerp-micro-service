import PropTypes from "prop-types";
import { getCategoryColor } from "../../../utils/color.util";
import CustomChip from "../../mui/chip";

const TaskCategory = ({ category, sx, ...props }) => {
  return (
    <CustomChip
      size="small"
      skin="light"
      sx={{ width: "fit-content", ...sx }}
      label={category.categoryName}
      color={getCategoryColor(category.categoryId)}
      {...props}
    />
  );
};

TaskCategory.propTypes = {
  category: PropTypes.object,
  sx: PropTypes.object,
};

export { TaskCategory };
