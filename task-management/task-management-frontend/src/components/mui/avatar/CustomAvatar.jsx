import MuiAvatar from "@mui/material/Avatar";
import { lighten } from "@mui/material/styles";
import { forwardRef, memo } from "react";
import { useBgColor } from "../../../hooks/useBgColor";
import { getRandomColorSkin } from "../../../utils/color.util";

// eslint-disable-next-line react/display-name
const Avatar = forwardRef((props, ref) => {
  // eslint-disable-next-line react/prop-types
  let { sx, src, skin, color } = props;

  if (!color) {
    color = getRandomColorSkin();
  }

  const bgColors = useBgColor();

  const getAvatarStyles = (skin, skinColor) => {
    let avatarStyles;

    if (skin === "light") {
      avatarStyles = { ...bgColors[`${skinColor}Light`] };
    } else if (skin === "light-static") {
      avatarStyles = {
        ...bgColors[`${skinColor}Filled`],
        backgroundColor: lighten(
          bgColors[`${skinColor}Filled`].backgroundColor,
          0.25
        ),
      };
    } else {
      avatarStyles = { ...bgColors[`${skinColor}Filled`] };
    }

    return avatarStyles;
  };

  const colors = {
    primary: getAvatarStyles(skin, "primary"),
    secondary: getAvatarStyles(skin, "secondary"),
    success: getAvatarStyles(skin, "success"),
    error: getAvatarStyles(skin, "error"),
    warning: getAvatarStyles(skin, "warning"),
    info: getAvatarStyles(skin, "info"),
  };

  return (
    <MuiAvatar
      ref={ref}
      {...props}
      sx={!src && skin && color ? Object.assign(colors[color], sx) : sx}
    />
  );
});

export default memo(Avatar);
