import {Box, Typography} from "@material-ui/core";
import Drawer from "@material-ui/core/Drawer";
import List from "@material-ui/core/List";
import makeStyles from "@material-ui/core/styles/makeStyles";
import PropTypes from "prop-types";
import React, {useEffect} from "react";
import {Link as RouterLink} from "react-router-dom";
import SimpleBar from "simplebar-react";
import "simplebar/dist/simplebar.min.css";
import PrimaryButton from "../../../component/button/PrimaryButton";
import {MENU_LIST} from "../../../config/menuconfig";
import {useAuthState} from "../../../state/AuthState";
import {fetchMenu} from "../../../state/MenuState";
import GroupMenuItem, {menuItemBaseStyle} from "./GroupMenuItem";
import {blackColor, whiteColor} from "./MenuItem";

export const drawerWidth = 300;
export const miniDrawerWidth = 50;

const useStyles = makeStyles((theme) => ({
  drawerPaper: {
    height: "calc(100% - 64px)",
    marginTop: 64,
    overflowX: "hidden",
    width: drawerWidth,
    flexShrink: 0,
    border: "none",
    // boxShadow: `2px 0px 1px -1px rgb(0 0 0 / 20%),
    //   1px 0px 1px 0px rgb(0 0 0 / 14%),
    //   1px 0px 3px 0px rgb(0 0 0 / 12%)`,
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: "nowrap",
  },
  signInText: {
    ...menuItemBaseStyle(theme).menuItemText,
    fontSize: "1rem",
    whiteSpace: "break-spaces",
    color: whiteColor,
    textAlign: "center",
    paddingBottom: 16,
  },
  signInContainer: {
    width: drawerWidth - 20,
    zIndex: theme.zIndex.drawer + 1,
    paddingTop: theme.spacing(3),
    paddingBottom: theme.spacing(6),
  },
  background: {
    position: "absolute",
    zIndex: "1",
    height: "100%",
    width: "100%",
    display: "block",
    top: "0",
    left: "0",
    backgroundSize: "cover",
    backgroundPosition: "center center",
    "&:after": {
      position: "absolute",
      zIndex: "3",
      width: "100%",
      height: "100%",
      content: '""',
      display: "block",
      background: blackColor,
      opacity: ".8",
    },
  },

  // sidebarWrapper: {
  //   // width: "100%",
  //   paddingTop: 75,
  //   position: "relative",
  //   height: "100vh",
  //   zIndex: "4",
  //   // transitionDuration: ".2s, .2s, .35s",
  //   // transitionProperty: "top, bottom, width",
  //   // transitionTimingFunction: "linear, linear, ease",
  // },
}));

export default function SideBar(props) {
  const classes = useStyles();
  const { open, image, color: bgColor } = props;
  const { isAuthenticated, isValidating } = useAuthState();

  useEffect(() => {
    if (isAuthenticated.get()) fetchMenu();
  }, [isAuthenticated.get()]);

  return (
    <Drawer
      variant="persistent"
      anchor="left"
      open={open}
      className={classes.drawer}
      classes={{
        paper: classes.drawerPaper,
      }}
    >
      {/* <div className={classNames(classes.sidebarWrapper)}> */}
      <SimpleBar
        style={{
          // marginTop: 64,
          marginBottom: 16,
          position: "relative",
          height: "100%",
          zIndex: "4",
          overflowX: "hidden",
          overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu'scrollbar reach end
        }}
      >
        <List component="nav">
          {MENU_LIST.map((group) => (
            <GroupMenuItem key={group.text} group={group} color={bgColor} />
          ))}
        </List>
      </SimpleBar>
      {!isAuthenticated.get() && !isValidating.get() && open && (
        <Box
          className={classes.signInContainer}
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          ml="auto"
          mr="auto"
        >
          <Typography className={classes.signInText}>
            Đăng nhập ngay để sử dụng những tính năng dành riêng cho bạn
          </Typography>
          <PrimaryButton
            component={RouterLink}
            to="/login"
            style={{ width: 160, borderRadius: 25 }}
          >
            Đăng nhập
          </PrimaryButton>
        </Box>
      )}
      {/* </div> */}
      {image && (
        <div
          className={classes.background}
          style={{ backgroundImage: "url(" + image + ")" }}
        />
      )}
    </Drawer>
  );
}

SideBar.propTypes = {
  bgColor: PropTypes.oneOf(["purple", "blue", "green", "orange", "red"]),
  image: PropTypes.string,
  open: PropTypes.bool,
};
