import MuiChip from "@mui/material/Chip";
import PropTypes from "prop-types";
import clsx from "clsx";
import { useBgColor } from "../../../hooks/useBgColor";

const Chip = (props) => {
  const { sx, skin, color, rounded } = props;

  const bgColors = useBgColor();

  const colors = {
    primary: { ...bgColors.primaryLight },
    secondary: { ...bgColors.secondaryLight },
    success: { ...bgColors.successLight },
    error: { ...bgColors.errorLight },
    warning: { ...bgColors.warningLight },
    info: { ...bgColors.infoLight },
  };

  const propsToPass = { ...props };

  propsToPass.rounded = undefined;

  return (
    <MuiChip
      {...propsToPass}
      variant="filled"
      className={clsx({
        "MuiChip-rounded": rounded,
        "MuiChip-light": skin === "light",
      })}
      sx={skin === "light" && color ? Object.assign(colors[color], sx) : sx}
    />
  );
};

// ** PropTypes
Chip.propTypes = {
  color: PropTypes.oneOf([
    "primary",
    "secondary",
    "success",
    "error",
    "warning",
    "info",
  ]),
  rounded: PropTypes.bool,
  skin: PropTypes.oneOf(["light", "dark"]),
  sx: PropTypes.object,
};

export default Chip;
