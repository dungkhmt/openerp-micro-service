import { default as MenuIcon } from "@mui/icons-material/Menu";
import { Box } from "@mui/material";
import AppBar from "@mui/material/AppBar";
import IconButton from "@mui/material/IconButton";
import SvgIcon from "@mui/material/SvgIcon";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import { styled } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import { ReactComponent as Logo } from "../assets/icons/logo.svg";
import bgImage from "../assets/img/sidebar-2.webp";
import PropTypes from "prop-types";
import React, { useState } from "react";
import AccountButton from "./account/AccountButton";
import NotificationButton from "./notification/NotificationButton";
import SideBar, { drawerWidth } from "./sidebar/SideBar";

const Offset = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

const Main = styled("main")(({ theme, isOpen }) => ({
  flexShrink: 1,
  flexGrow: 1,
  maxWidth: "100%",
  padding: theme.spacing(3),
  transition: theme.transitions.create(["maxWidth", "margin"], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  marginLeft: -drawerWidth,
  ...(isOpen
    ? {
        maxWidth: "calc(100% - 300px)",
        transition: theme.transitions.create(["maxWidth", "margin"], {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
        marginLeft: 0,
      }
    : {}),
}));

const styles = {
  root: {
    display: "flex",
  },
  appBar: (theme) => ({
    // position: "sticky", // sticky is not supported by IE11.
    top: 0,
    transition: theme.transitions.create("top"),
    backdropFilter: "blur(20px)",
    boxShadow: `inset 0px -1px 1px ${theme.palette.grey[100]}`,
    backgroundColor: "rgba(255,255,255,0.72)",
    zIndex: theme.zIndex.drawer + 1,
  }),
  menuButton: {
    marginRight: 3,
    width: 48,
    height: 48,
  },
  sectionDesktop: (theme) => ({
    display: "none",
    [theme.breakpoints.up("md")]: {
      display: "flex",
      alignItems: "center",
    },
  }),
  appName: (theme) => ({
    paddingLeft: 0.5,
    display: "none",
    [theme.breakpoints.up("sm")]: {
      display: "block",
    },
  }),
};

function Layout({ children }) {
  //
  const { keycloak } = useKeycloak();

  //
  const [open, setOpen] = React.useState(true);
  const [image] = useState(bgImage);
  const [color] = useState("blue");

  return (
    <Box sx={styles.root}>
      <AppBar position="fixed" color="inherit" sx={styles.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={() => setOpen(!open)}
            edge="start"
            sx={styles.menuButton}
          >
            <MenuIcon />
          </IconButton>
          <SvgIcon fontSize="large">
            <Logo width={20} height={20} x={2} y={2} />
          </SvgIcon>

          <Typography sx={styles.appName} variant="h6" noWrap>
            Open ERP
          </Typography>

          {/* Use this div tag to push the icons to the right */}
          <div style={{ flexGrow: 1 }} />
          <Box sx={styles.sectionDesktop}>
            {keycloak.authenticated && (
              <>
                <NotificationButton />
                <AccountButton />
              </>
            )}
          </Box>
        </Toolbar>
      </AppBar>
      <SideBar open={open} image={image} color={color} />
      <Main isOpen={open}>
        <Offset />
        {children}
      </Main>
    </Box>
  );
}

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;
