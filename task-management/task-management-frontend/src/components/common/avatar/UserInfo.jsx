import { Typography, Box } from "@mui/material";
import { UserAvatar } from "./UserAvatar";
import PropTypes from "prop-types";
const UserInfo = ({
  user,
  typographySx = {},
  variant = "subtitle2",
  showEmail = false,
}) => {
  const fullName =
    user.firstName || user.lastName
      ? `${user.firstName ?? ""} ${user.lastName ?? ""}`.trim()
      : " - ";
  return (
    <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
      <UserAvatar user={user} skin="light" />
      <Box
        sx={{
          display: "flex",
          alignItems: "flex-start",
          flexDirection: "column",
        }}
      >
        <Typography variant={variant} sx={typographySx}>
          {`${fullName}`}
        </Typography>
        {showEmail && (
          <Typography noWrap variant="caption">
            {user.email || "No email provided"}
          </Typography>
        )}
      </Box>
    </Box>
  );
};

UserInfo.propTypes = {
  user: PropTypes.object.isRequired,
  typographySx: PropTypes.object,
  variant: PropTypes.string,
  showEmail: PropTypes.bool,
};

export default UserInfo;
