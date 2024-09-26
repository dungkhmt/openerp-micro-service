import PropTypes from "prop-types";
import { getStatusColor } from "../../../utils/color.util";
import CustomChip from "../../mui/chip";

const TaskStatus = ({ status, sx, ...props }) => {
  return (
    <CustomChip
      title={`Trạng thái: ${status.description}`}
      size="small"
      skin="light"
      sx={{ width: "fit-content", ...sx }}
      label={status.description}
      color={getStatusColor(status.statusId)}
      {...props}
    />
  );
};

TaskStatus.propTypes = {
  status: PropTypes.object,
  sx: PropTypes.object,
};

export { TaskStatus };
