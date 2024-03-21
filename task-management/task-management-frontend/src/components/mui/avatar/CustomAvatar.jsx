import MuiAvatar from "@mui/material/Avatar";
import { useTheme } from "@mui/material/styles";
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

  const theme = useTheme();
  const bgColors = useBgColor();

  const getAvatarStyles = (skin, skinColor) => {
    let avatarStyles;

    if (skin === "light") {
      avatarStyles = { ...bgColors[`${skinColor}Light`] };
    } else if (skin === "light-static") {
      avatarStyles = {
        color: bgColors[`${skinColor}Light`].color,
        backgroundColor: lighten(theme.palette[skinColor].main, 0.88),
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
