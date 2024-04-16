import { forwardRef } from "react";
import PropTypes from "prop-types";
import CustomAvatar from "../../mui/avatar/CustomAvatar";
import { getRandomColorSkin } from "../../../utils/color.util";

const UserAvatar = forwardRef(function UserAvatar(
  { user, width = 30, height = 30, fontSize = "0.875rem", ...props },
  ref
) {
  return (
    <CustomAvatar
      skin="light"
      color={getRandomColorSkin(user.id)}
      {...props}
      sx={{
        width,
        height,
        fontSize,
        ...props.sx,
      }}
      src={user.avatarUrl}
      ref={ref}
    >
      {`${user.firstName?.charAt(0) ?? ""}${user.lastName?.charAt(0) ?? ""}`}
    </CustomAvatar>
  );
});

UserAvatar.propTypes = {
  user: PropTypes.object.isRequired,
  width: PropTypes.number,
  height: PropTypes.number,
  fontSize: PropTypes.string,
  sx: PropTypes.object,
};

export { UserAvatar };
