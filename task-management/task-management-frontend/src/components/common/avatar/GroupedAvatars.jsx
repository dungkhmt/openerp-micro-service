import MuiAvatar from "@mui/material/Avatar";
import { Box, Tooltip, useMediaQuery, useTheme } from "@mui/material";
import PropTypes from "prop-types";
import { UserAvatar } from "./UserAvatar";

const GroupedAvatars = ({ users, max_displayed_users = 5 }) => {
  const theme = useTheme();
  const isSm = useMediaQuery(theme.breakpoints.only("sm"));
  const isMd = useMediaQuery(theme.breakpoints.only("md"));
  const isLg = useMediaQuery(theme.breakpoints.up("lg"));

  // Determine max avatars dynamically based on screen size
  let responsiveLimit = 3;
  if (isSm) responsiveLimit = 4;
  else if (isMd) responsiveLimit = 5;
  else if (isLg) responsiveLimit = 6;

  const maxAvatars = Math.min(max_displayed_users, responsiveLimit);
  const displayedUsers = users.slice(0, maxAvatars);
  const hiddenUsersCount = users.length - displayedUsers.length;

  return (
    <Box sx={{ display: "inline-flex", alignItems: "center" }}>
      {displayedUsers.map((user, index) => (
        <UserAvatar
          key={index}
          user={user}
          skin="light-static"
          sx={{
            marginLeft: index === 0 ? 0 : -1.5,
            border: "1px solid white",
            zIndex: index,
          }}
        />
      ))}
      {hiddenUsersCount > 0 && (
        <Tooltip title={`${hiddenUsersCount} more users`}>
          <MuiAvatar
            sx={{
              marginLeft: -1.5,
              border: "1px solid white",
              backgroundColor: "#A9A9A9",
              color: "white",
              width: 30,
              height: 30,
              fontSize: "0.875rem",
            }}
          >
            {hiddenUsersCount}+
          </MuiAvatar>
        </Tooltip>
      )}
    </Box>
  );
};

GroupedAvatars.propTypes = {
  users: PropTypes.arrayOf(PropTypes.object).isRequired,
  max_displayed_users: PropTypes.number.isRequired,
};

export { GroupedAvatars };
