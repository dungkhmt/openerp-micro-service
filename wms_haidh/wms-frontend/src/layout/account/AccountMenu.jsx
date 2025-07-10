import { useState } from "@hookstate/core";
import AccountCircleRoundedIcon from "@mui/icons-material/AccountCircleRounded";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import FeedbackIcon from "@mui/icons-material/Feedback";
import {
  Avatar,
  Box,
  ClickAwayListener,
  Divider,
  Grow,
  ListItemAvatar,
  ListItemText,
  MenuItem,
  MenuList,
  Paper,
  Popper,
  Typography,
} from "@mui/material";
import { grey } from "@mui/material/colors";
import { styled } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import { lazy } from "react";

const FeedbackDialog = lazy(() => import("./FeedbackDialog"));

export const StyledMenuItem = styled(MenuItem)({
  padding: "0px 8px",
  borderRadius: 8,
  "&:hover": {
    backgroundColor: "#f5f5f5",
  },
});

const styles = {
  paper: {
    maxHeight: `calc(100vh - 80px)`,
    minWidth: 240,
    borderRadius: 2,
    overflowY: "hidden",
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  divider: (theme) => ({
    marginRight: theme.spacing(2),
    marginLeft: theme.spacing(2),
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  }),
  avatar: (theme) => ({
    // width: 60,
    // height: 60,
    // fontSize: "1.875rem",
    marginRight: 1.5,
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  }),
  avatarIcon: {
    // color: theme.palette.getContrastText(deepOrange[500]),
    backgroundColor: grey[300],
    margin: "8px 12px 8px 0px",
  },
  text: (theme) => ({
    fontWeight: theme.typography.fontWeightMedium,
  }),
};

export const iconStyles = { color: "black" };
export const menuItemWrapperStyles = { padding: "0px 8px" };

export function AccountMenu(props) {
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const accountUrl = keycloak.createAccountUrl();

  //
  const { open, id, anchorRef, avatarBgColor } = props;
  const openFeedback = useState(false);

  // Menu
  const handleClose = (event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return;
    }

    open.set(false);
  };

  function handleListKeyDown(event) {
    if (event.key === "Tab") {
      event.preventDefault();
      open.set(false);
    }
  }

  //
  const handleOpenFeedbackDialog = (event) => {
    handleClose(event);
    openFeedback.set(true);
  };

  const handleViewAccount = (event) => {
    handleClose(event);
    window.location.href = accountUrl;
  };

  const handleLogout = () => {
    const logoutOptions = {
      redirectUri: window.location.origin + "/",
    };
    sessionStorage.clear();
    keycloak.logout(logoutOptions);
  };

  const menuItems = [
    { topDivider: true },
    {
      text: "Đóng góp ý kiến",
      subheader: "Góp phần cải thiện phiên bản Open ERP mới.",
      onClick: handleOpenFeedbackDialog,
      icon: (
        <FeedbackIcon
          style={iconStyles}
          // fontSize="medium"
        />
      ),
    },
    { topDivider: true },
    {
      text: "Tài khoản",
      onClick: handleViewAccount,
      icon: (
        <AccountCircleRoundedIcon
          style={iconStyles}
          // fontSize="medium"
        />
      ),
    },

    { topDivider: true },
    {
      text: "Đăng xuất",
      onClick: handleLogout,
      icon: (
        <ExitToAppIcon
          style={iconStyles}
          // fontSize="medium"
        />
      ),
    },
  ];

  return (
    <>
      <Popper
        open={open.get()}
        anchorEl={anchorRef.current}
        role={undefined}
        transition
        disablePortal
        modifiers={[
          {
            name: "flip",
            enabled: false,
          },
          {
            name: "preventOverflow",
            enabled: true,
            options: {
              padding: 36,
              boundary: "scrollParent",
            },
          },
        ]}
      >
        {({ TransitionProps, placement }) => (
          <Grow
            {...TransitionProps}
            style={{
              transformOrigin:
                placement === "bottom" ? "center top" : "center bottom",
            }}
          >
            <Paper elevation={0} sx={styles.paper}>
              <ClickAwayListener onClickAway={handleClose}>
                <MenuList
                  id={id}
                  autoFocusItem={open.get()}
                  onKeyDown={handleListKeyDown}
                >
                  <li style={menuItemWrapperStyles}>
                    <Box display="flex" pl={1} pr={1} alignItems="center">
                      <Avatar
                        sx={styles.avatar}
                        style={{
                          background: avatarBgColor,
                        }}
                      >
                        {token.name
                          ?.split(" ")
                          .pop()
                          .substring(0, 1)
                          .toLocaleUpperCase()}
                      </Avatar>

                      <Box
                        display="flex"
                        flexGrow={1}
                        alignItems="center"
                        flexShrink={1}
                      >
                        <Typography sx={styles.text}>{token.name}</Typography>
                      </Box>
                    </Box>
                  </li>

                  {menuItems.map(
                    ({ text, subheader, topDivider, onClick, icon }, index) =>
                      topDivider ? (
                        <Divider key={index} sx={styles.divider} />
                      ) : (
                        <div key={text} style={menuItemWrapperStyles}>
                          <StyledMenuItem onClick={onClick}>
                            <ListItemAvatar>
                              <Avatar sx={styles.avatarIcon}>{icon}</Avatar>
                            </ListItemAvatar>
                            <ListItemText
                              primary={text}
                              secondary={subheader}
                              primaryTypographyProps={{
                                sx: styles.text,
                              }}
                            />
                          </StyledMenuItem>
                        </div>
                      )
                  )}
                </MenuList>
              </ClickAwayListener>
            </Paper>
          </Grow>
        )}
      </Popper>
      <FeedbackDialog open={openFeedback} />
    </>
  );
}
