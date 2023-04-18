import {Downgraded, useState} from "@hookstate/core";
import {IconButton} from "@material-ui/core";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import {grey} from "@material-ui/core/colors";
import ListItemText from "@material-ui/core/ListItemText";
import MenuList from "@material-ui/core/MenuList";
import Paper from "@material-ui/core/Paper";
import Popper from "@material-ui/core/Popper";
import {makeStyles} from "@material-ui/core/styles";
import CheckRoundedIcon from "@material-ui/icons/CheckRounded";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import React, {useEffect} from "react";
import {request} from "../../api";
import {useNotificationState} from "../../state/NotificationState";
import {iconStyles, menuItemWrapperStyles, StyledMenuItem,} from "../account/AccountMenu";
import {STATUS_NOTIFICATION_READ} from "./Notification";
import {notificationMenuWidth} from "./NotificationMenu";

const menuId = "more setting";
const menuItemIconStyles = { ...iconStyles, marginRight: 12 };

const useStyles = makeStyles((theme) => ({
  iconButton: {
    padding: 0,
    "&:hover": {
      backgroundColor: grey[200],
    },
  },
  paper: {
    backgroundColor: "#ffffff",
    width: notificationMenuWidth - 16,
    marginLeft: -8,
    minWidth: 240,
    borderRadius: 8,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  text: {
    fontWeight: theme.typography.fontWeightMedium,
  },
  popper: {
    zIndex: 1,
    '&[x-placement*="bottom"] $arrow': {
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
    '&[x-placement*="top"] $arrow': {
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
    '&[x-placement*="right"] $arrow': {
      left: 0,
      marginLeft: "-0.9em",
      height: "3em",
      width: "1em",
      "&::before": {
        borderWidth: "1em 1em 1em 0",
        borderColor: `transparent ${theme.palette.background.paper} transparent transparent`,
      },
    },
    '&[x-placement*="left"] $arrow': {
      right: 0,
      marginRight: "-0.9em",
      height: "3em",
      width: "1em",
      "&::before": {
        borderWidth: "1em 0 1em 1em",
        borderColor: `transparent transparent transparent ${theme.palette.background.paper}`,
      },
    },

    // Hide popper when it scrolls outside of its boundaries
    "&[x-out-of-boundaries]": {
      opacity: 0,
      pointerEvents: "none",
    },
  },
  arrow: {
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
  },
  menuItem: {
    padding: "2px 8px",
  },
}));

/**
 * See: https://github.com/mui-org/material-ui/blob/4f2a07e140c954b478a6670c009c23a59ec3e2d4/docs/src/pages/components/popper/ScrollPlayground.js
 */
function ActionsWithNotificationButton() {
  const classes = useStyles();

  //
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
        className={classes.iconButton}
        component="span"
        ref={anchorRef}
        aria-haspopup="true"
        aria-label="action with notification"
        aria-controls={open.get() ? menuId : undefined}
        onClick={handleToggle}
      >
        <MoreHorizIcon style={{ fontSize: 32, padding: 4 }} />
      </IconButton>
      <Popper
        transition
        disablePortal
        open={open.get()}
        role={undefined}
        placement="bottom-end"
        anchorEl={anchorRef.current}
        className={classes.popper}
        modifiers={{
          flip: {
            enabled: false,
          },
          preventOverflow: {
            enabled: true,
            boundariesElement: "scrollParent",
          },
          arrow: {
            enabled: true,
            element: arrowRef,
          },
        }}
      >
        <span className={classes.arrow} ref={setArrowRef} />
        <Paper elevation={0} className={classes.paper}>
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
                    className={classes.menuItem}
                  >
                    {icon}
                    <ListItemText
                      component="span"
                      primary={text}
                      primaryTypographyProps={{
                        className: classes.text,
                      }}
                    />
                  </StyledMenuItem>
                </div>
              ))}
            </MenuList>
          </ClickAwayListener>
        </Paper>
      </Popper>
    </>
  );
}

export default ActionsWithNotificationButton;
