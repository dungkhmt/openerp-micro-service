import { Downgraded, useState } from "@hookstate/core";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";
import { Grow, IconButton } from "@mui/material";
import ClickAwayListener from "@mui/material/ClickAwayListener";
import ListItemText from "@mui/material/ListItemText";
import MenuList from "@mui/material/MenuList";
import Paper from "@mui/material/Paper";
import MuiPopper from "@mui/material/Popper";
import { grey } from "@mui/material/colors";
import { styled } from "@mui/material/styles";
import { request } from "../../api";
import React, { useEffect } from "react";
import { useNotificationState } from "../../state/NotificationState";
import {
  StyledMenuItem,
  iconStyles,
  menuItemWrapperStyles,
} from "../account/AccountMenu";
import { STATUS_NOTIFICATION_READ } from "./Notification";

const menuId = "more setting";
const menuItemIconStyles = { ...iconStyles, marginRight: 12 };

export const notificationMenuWidth = 360;
const styles = {
  iconButton: {
    padding: 0,
    "&:hover": {
      backgroundColor: grey[200],
    },
  },
  paper: {
    backgroundColor: "#ffffff",
    width: notificationMenuWidth - 16,
    marginLeft: -1,
    minWidth: 240,
    borderRadius: 2,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  text: (theme) => ({
    fontWeight: theme.typography.fontWeightMedium,
  }),
  menuItem: {
    padding: "2px 8px",
  },
};

const Popper = styled(MuiPopper, {
  shouldForwardProp: (prop) => prop !== "arrow",
})(({ theme, arrow }) => ({
  zIndex: 1,
  "& > div": {
    position: "relative",
  },
  '&[data-popper-placement*="bottom"]': {
    "& > div": {
      marginTop: arrow ? 2 : 0,
    },
    "& .MuiPopper-arrow": {
      top: 0,
      left: 0,
      marginTop: "-0.9em",
      width: "3em",
      height: "1em",
      "&::before": {
        borderWidth: "0 1em 1em 1em",
        borderColor: `transparent transparent ${theme.palette.background.paper} transparent`,
      },
    },
  },
  '&[data-popper-placement*="top"]': {
    "& > div": {
      marginBottom: arrow ? 2 : 0,
    },
    "& .MuiPopper-arrow": {
      bottom: 0,
      left: 0,
      marginBottom: "-0.9em",
      width: "3em",
      height: "1em",
      "&::before": {
        borderWidth: "1em 1em 0 1em",
        borderColor: `${theme.palette.background.paper} transparent transparent transparent`,
      },
    },
  },
  '&[data-popper-placement*="right"]': {
    "& > div": {
      marginLeft: arrow ? 2 : 0,
    },
    "& .MuiPopper-arrow": {
      left: 0,
      marginLeft: "-0.9em",
      height: "3em",
      width: "1em",
      "&::before": {
        borderWidth: "1em 1em 1em 0",
        borderColor: `transparent ${theme.palette.background.paper} transparent transparent`,
      },
    },
  },
  '&[data-popper-placement*="left"]': {
    "& > div": {
      marginRight: arrow ? 2 : 0,
    },
    "& .MuiPopper-arrow": {
      right: 0,
      marginRight: "-0.9em",
      height: "3em",
      width: "1em",
      "&::before": {
        borderWidth: "1em 0 1em 1em",
        borderColor: `transparent transparent transparent ${theme.palette.background.paper}`,
      },
    },
  },
}));

const Arrow = styled("div")({
  position: "absolute",
  fontSize: 7,
  width: "3em",
  height: "3em",
  "&::before": {
    content: '""',
    margin: "auto",
    display: "block",
    width: 0,
    height: 0,
    borderStyle: "solid",
  },
});

/**
 * See: https://github.com/mui/material-ui/blob/master/docs/data/material/components/popper/ScrollPlayground.js
 */
function ActionsWithNotificationButton() {
  const open = useState(false);
  const [arrowRef, setArrowRef] = React.useState(null);
  const { notifications, numUnRead } = useNotificationState();

  // Return focus to the button when we transitioned from !open -> open
  const prevOpen = React.useRef(open.get());
  const anchorRef = React.useRef(null);

  // Action button
  const handleToggle = () => {
    open.set((prevOpen) => !prevOpen);
  };

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

  useEffect(() => {
    if (prevOpen.current === true && open.get() === false) {
      anchorRef.current.focus();
    }

    prevOpen.current = open.get();
  }, [open.get()]);

  // Menu items
  const handleMarksAllAsRead = (event) => {
    handleClose(event);

    if (numUnRead.get() > 0)
      request(
        "patch",
        "/notification/status",
        (res) => {
          const rawNotifications = notifications.attach(Downgraded).get();

          rawNotifications.forEach((notification) => {
            notification.read = true;
          });

          notifications.set(rawNotifications);
          numUnRead.set(0);
        },
        {},
        {
          status: STATUS_NOTIFICATION_READ,
          beforeOrAt: new Date(notifications[0].time.get()),
        }
      );
  };

  const menuItems = [
    {
      text: "Đánh dấu tất cả là đã đọc",
      onClick: handleMarksAllAsRead,
      icon: <CheckRoundedIcon style={menuItemIconStyles} fontSize="small" />,
    },
  ];

  return (
    <>
      <IconButton
        component="span"
        aria-haspopup="true"
        aria-label="action with notification"
        aria-controls={open.get() ? menuId : undefined}
        ref={anchorRef}
        onClick={handleToggle}
        sx={styles.iconButton}
      >
        <MoreHorizIcon style={{ fontSize: 32, padding: 4 }} />
      </IconButton>
      <Popper
        transition
        disablePortal
        open={open.get()}
        placement="bottom-end"
        anchorEl={anchorRef.current}
        modifiers={[
          {
            name: "arrow",
            enabled: true,
            options: {
              element: arrowRef,
            },
          },
        ]}
      >
        {({ TransitionProps, placement }) => (
          <>
            <Arrow ref={setArrowRef} className="MuiPopper-arrow" />
            <Grow
              {...TransitionProps}
              style={{
                transformOrigin: placement.startsWith("bottom")
                  ? "center top"
                  : "center bottom",
              }}
            >
              <Paper elevation={0} sx={styles.paper}>
                <ClickAwayListener onClickAway={handleClose}>
                  <MenuList
                    id={menuId}
                    autoFocusItem={open.get()}
                    onKeyDown={handleListKeyDown}
                  >
                    {menuItems.map(({ text, onClick, icon }) => (
                      <div key={text} style={menuItemWrapperStyles}>
                        <StyledMenuItem
                          onClick={onClick}
                          sx={styles.menuItem}
                          style={{ minHeight: 36 }}
                        >
                          {icon}
                          <ListItemText
                            component="span"
                            primary={text}
                            primaryTypographyProps={{
                              sx: styles.text,
                            }}
                          />
                        </StyledMenuItem>
                      </div>
                    ))}
                  </MenuList>
                </ClickAwayListener>
              </Paper>
            </Grow>
          </>
        )}
      </Popper>
    </>
  );
}

export default ActionsWithNotificationButton;
