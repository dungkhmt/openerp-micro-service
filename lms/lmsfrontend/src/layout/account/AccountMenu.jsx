import {useState} from "@hookstate/core";
import {Avatar, Box, Divider, ListItemAvatar, MenuItem, Typography} from "@material-ui/core";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import {grey} from "@material-ui/core/colors";
import Grow from "@material-ui/core/Grow";
import ListItemText from "@material-ui/core/ListItemText";
import MenuList from "@material-ui/core/MenuList";
import Paper from "@material-ui/core/Paper";
import Popper from "@material-ui/core/Popper";
import {makeStyles, withStyles} from "@material-ui/core/styles";
import AccountCircleRoundedIcon from "@material-ui/icons/AccountCircleRounded";
import ExitToAppIcon from "@material-ui/icons/ExitToApp";
import FeedbackIcon from "@material-ui/icons/Feedback";
import VpnKeyRoundedIcon from "@material-ui/icons/VpnKeyRounded";
import React, {lazy} from "react";
import {useDispatch} from "react-redux";
import {useHistory} from "react-router-dom";
import {logout} from "../../action";

const FeedbackDialog = lazy(() => import("./FeedbackDialog"));

// const StyledMenu = withStyles({
//   paper: {
//     minWidth: 240,
//     borderRadius: 8,
//     boxShadow:
//       "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
//   },
// })((props) => (
//   <Menu
//     getContentAnchorEl={null}
//     anchorOrigin={{
//       vertical: "bottom",
//       horizontal: "center",
//     }}
//     transformOrigin={{
//       vertical: "top",
//       horizontal: "center",
//     }}
//     {...props}
//   />
// ));

export const StyledMenuItem = withStyles((theme) => ({
  root: {
    padding: "0px 8px",
    borderRadius: 8,
    "&:hover": {
      backgroundColor: "#f5f5f5",
    },
  },
}))(MenuItem);

const useStyles = makeStyles((theme) => ({
  paper: {
    maxHeight: `calc(100vh - 80px)`,
    minWidth: 240,
    borderRadius: 8,
    overflowY: "hidden",
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  divider: {
    marginRight: theme.spacing(2),
    marginLeft: theme.spacing(2),
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },
  avatar: {
    // width: 60,
    // height: 60,
    // fontSize: "1.875rem",
    marginRight: 12,
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
  },

  avatarIcon: {
    // color: theme.palette.getContrastText(deepOrange[500]),
    backgroundColor: grey[300],
    margin: "8px 12px 8px 0px",
  },
  text: {
    fontWeight: theme.typography.fontWeightMedium,
  },
}));

export const iconStyles = { color: "black" };
export const menuItemWrapperStyles = { padding: "0px 8px" };

export function AccountMenu(props) {
  const classes = useStyles();
  const history = useHistory();
  const dispatch = useDispatch();

  const { open, id, anchorRef, user, avatarBgColor } = props;
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
  const handlePasswordChange = (event) => {
    handleClose(event);
    history.push(`/userlogin/change-password/${user.userName.get()}`);
  };

  const handleViewAccount = (event) => {
    handleClose(event);
    history.push(`/userlogin/${user.partyId.get()}`);
  };

  const handleLogout = () => dispatch(logout());

  const handleOpenFeedbackDialog = (event) => {
    handleClose(event);
    openFeedback.set(true);
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
    {
      text: "Đổi mật khẩu",
      onClick: handlePasswordChange,
      icon: (
        <VpnKeyRoundedIcon
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
        modifiers={{
          flip: {
            enabled: false,
          },
          preventOverflow: {
            enabled: true,
            boundariesElement: "scrollParent",
          },
        }}
      >
        {({ TransitionProps, placement }) => (
          <Grow
            {...TransitionProps}
            style={{
              transformOrigin:
                placement === "bottom" ? "center top" : "center bottom",
            }}
          >
            <Paper elevation={0} className={classes.paper}>
              <ClickAwayListener onClickAway={handleClose}>
                <MenuList
                  id={id}
                  autoFocusItem={open.get()}
                  onKeyDown={handleListKeyDown}
                >
                  <li style={menuItemWrapperStyles}>
                    <Box display="flex" pl={1} pr={1} alignItems="center">
                      <Avatar
                        className={classes.avatar}
                        style={{
                          background: avatarBgColor,
                        }}
                      >
                        {user.name.get()
                          ? user.name.get().substring(0, 1).toLocaleUpperCase()
                          : ""}
                      </Avatar>

                      <Box
                        display="flex"
                        flexGrow={1}
                        alignItems="center"
                        flexShrink={1}
                      >
                        <Typography className={classes.text}>
                          {user.name.get()}
                        </Typography>
                      </Box>
                    </Box>
                  </li>

                  {menuItems.map(
                    ({ text, subheader, topDivider, onClick, icon }, index) =>
                      topDivider ? (
                        <Divider key={index} className={classes.divider} />
                      ) : (
                        <div key={text} style={menuItemWrapperStyles}>
                          <StyledMenuItem onClick={onClick}>
                            <ListItemAvatar>
                              <Avatar className={classes.avatarIcon}>
                                {icon}
                              </Avatar>
                            </ListItemAvatar>
                            <ListItemText
                              primary={text}
                              secondary={subheader}
                              primaryTypographyProps={{
                                className: classes.text,
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
