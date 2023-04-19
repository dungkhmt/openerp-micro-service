import AppBar from "@material-ui/core/AppBar";
import IconButton from "@material-ui/core/IconButton";
import {makeStyles, styled} from "@material-ui/core/styles";
import SvgIcon from "@material-ui/core/SvgIcon";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import {default as MenuIcon} from "@material-ui/icons/Menu";
import clsx from "clsx";
import React, {useEffect, useState} from "react";
import {useLocation} from "react-router-dom";
import {ReactComponent as Logo} from "../assets/icons/logo.svg";
import bgImage from "../assets/img/sidebar-2.webp";
import {useAuthState} from "../state/AuthState";
import AccountButton from "./account/AccountButton";
import LanguageSwitch from "./languageswitcher/LanguageSwitch";
import NotificationButton from "./notification/NotificationButton";
import SideBar, {drawerWidth} from "./sidebar/v1/SideBar";

/**
 * https://mui.com/material-ui/react-app-bar/#fixed-placement
 */
const Offset = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
  },
  appBar: {
    // position: "sticky", // sticky is not supported by IE11.
    top: 0,
    transition: theme.transitions.create("top"),
    backdropFilter: "blur(20px)",
    boxShadow: `inset 0px -1px 1px ${theme.palette.grey[100]}`,
    backgroundColor: "rgba(255,255,255,0.72)",
    zIndex: theme.zIndex.drawer + 1,
  },
  menuButton: {
    marginRight: 24,
  },
  sectionDesktop: {
    display: "none",
    [theme.breakpoints.up("md")]: {
      display: "flex",
      alignItems: "center",
    },
  },
  appName: {
    paddingLeft: 4,
    display: "none",
    [theme.breakpoints.up("sm")]: {
      display: "block",
    },
  },
  content: {
    flexShrink: 1,
    flexGrow: 1,
    maxWidth: "100%",
    padding: theme.spacing(3),
    transition: theme.transitions.create(["maxWidth", "margin"], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth,
  },
  contentShift: {
    maxWidth: "calc(100% - 300px)",
    transition: theme.transitions.create(["maxWidth", "margin"], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  },
}));

function Layout({ children }) {
  const classes = useStyles();
  const { pathname } = useLocation();
  const isMeeting = pathname.startsWith("/chat/voice/main");

  //
  const { isAuthenticated } = useAuthState();

  //
  const [open, setOpen] = React.useState(true);
  const [image] = useState(bgImage);
  const [color] = useState("blue");

  useEffect(() => {
    if (isMeeting) setOpen(false);
  }, [isMeeting]);

  return (
    <div className={classes.root}>
      <AppBar position="fixed" color="inherit" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={() => setOpen(!open)}
            edge="start"
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
          <SvgIcon fontSize="large">
            <Logo width={20} height={20} x={2} y={2} />
          </SvgIcon>

          <Typography className={classes.appName} variant="h6" noWrap>
            Open ERP
          </Typography>

          {/* Use this div tag to push the icons to the right */}
          <div style={{ flexGrow: 1 }} />
          <div className={classes.sectionDesktop}>
            <LanguageSwitch />
            {isAuthenticated.get() && (
              <>
                <NotificationButton />
                <AccountButton />
              </>
            )}
          </div>
        </Toolbar>
      </AppBar>
      <SideBar open={open} image={image} color={color} />
      <main
        className={clsx(classes.content, {
          [classes.contentShift]: open,
        })}
      >
        <Offset />
        {children}
      </main>
    </div>
  );
}

export default Layout;
