import MuiAvatar from "@mui/material/Avatar";
import { Box, Tooltip } from "@mui/material";
import PropTypes from "prop-types";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";

const ViewEventMembers = ({ users, max_displayed_users }) => {
  const displayedUsers = users.slice(0, max_displayed_users);
  const hiddenUsersCount = users.length - displayedUsers.length;

  return (
    <Box
      sx={{
        display: "inline-flex",
        alignItems: "center",
      }}
    >
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
              color:"white",
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

ViewEventMembers.propTypes = {
  users: PropTypes.arrayOf(PropTypes.object).isRequired,
  max_displayed_users: PropTypes.number.isRequired,
};

export { ViewEventMembers };
