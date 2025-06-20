import { Icon } from "@iconify/react";
import { Box, LinearProgress, Typography, useMediaQuery } from "@mui/material";
import AppBar from "@mui/material/AppBar";
import IconButton from "@mui/material/IconButton";
import SvgIcon from "@mui/material/SvgIcon";
import Toolbar from "@mui/material/Toolbar";
import { styled } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import React, { Suspense, useEffect } from "react";
import { Outlet, useLocation } from "react-router";
import { ReactComponent as Logo } from "../assets/icons/logo.svg";
import { useNotificationState } from "../state/NotificationState";
import AccountButton from "./components/account/AccountButton";
import NotificationButton from "./components/notification/NotificationButton";
import SideBar, { collapsedNavWidth, navWidth } from "./SideBar";
import AutocompleteComponent from "./components/autocomplete/AutoComplete";
import Organization from "./components/organization/Organization";
import { useDispatch } from "react-redux";
import { useSelector } from "react-redux";
import {
  fetchLastOrganizationByMe,
  fetchOrganizationsByMe,
} from "../store/organization";

const Offset = styled("div")(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
  justifyContent: "flex-end",
}));

const Main = styled("main")(({ theme, isHidden, navCollapsed }) => {
  const ml = !isHidden ? (navCollapsed ? collapsedNavWidth : navWidth) : 0;
  return {
    flexShrink: 1,
    flexGrow: 1,
    maxWidth: "100%",
    padding: theme.spacing(3),
    paddingRight: 0,
    transition: theme.transitions.create(["maxWidth", "margin"], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -ml,
    ...(!isHidden
      ? {
          maxWidth: `calc(100% - ${ml}px)`,
          transition: theme.transitions.create(["maxWidth", "margin"], {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
          }),
          marginLeft: 0,
        }
      : {}),
  };
});

const styles = {
  root: {
    display: "flex",
    height: "100vh",
    overflowY: "hidden",
  },
  appBar: (theme) => ({
    // position: "sticky", // sticky is not supported by IE11.
    top: 0,
    transition: theme.transitions.create("top"),
    backdropFilter: "blur(20px)",
    // boxShadow: `inset 0px -1px 1px ${theme.palette.grey[100]}`,
    backgroundColor: "#323452",
    color: "#ddeeddee",
    zIndex: theme.zIndex.drawer + 1,
    height: "40px",
    "& .MuiToolbar-root": {
      height: "40px",
    },
  }),
  appName: (theme) => ({
    display: "none",
    color: "#ddeeddee",
    [theme.breakpoints.up("sm")]: {
      display: "block",
    },
  }),
};

function Layout() {
  const { keycloak } = useKeycloak();
  const dispatch = useDispatch();
  const {fetchLoading, currentOrganization } = useSelector((state) => state.organization);
  const [navCollapsed, setNavCollapsed] = React.useState(false);
  // ** For mobile
  const [navVisible, setNavVisible] = React.useState(false);

  // get path
  const location = useLocation();

  const notificationState = useNotificationState();

  // Route-based exclusion logic
  const excludedPaths = ["/invitations", "/new-organization"];
  const isExcludedRoute = excludedPaths.some((path) =>
    location.pathname.startsWith(path)
  );
  const showLayoutComponents = keycloak.authenticated && !isExcludedRoute;

  const hidden =
    useMediaQuery((theme) => theme.breakpoints.down("lg")) ||
    !showLayoutComponents;

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname, notificationState.open]);

  useEffect(() => {
    const fetchOrgs = async () => {
      try {
        await dispatch(fetchLastOrganizationByMe()).unwrap();
        await dispatch(fetchOrganizationsByMe()).unwrap();
      } catch (err) {
        console.error("Failed to fetch organizations", err);
      }
    };

    if (keycloak.authenticated) {
      fetchOrgs();
    }
  }, [dispatch, keycloak.authenticated]);

  if (fetchLoading && !currentOrganization) {
    return <LinearProgress />;
  }

  return (
    <Suspense fallback={<LinearProgress />}>
      <Box sx={styles.root}>
        <AppBar position="fixed" color="inherit" sx={styles.appBar}>
          <Toolbar>
            {showLayoutComponents && (
              <IconButton
                color="inherit"
                aria-label="open drawer"
                onClick={() =>
                  hidden
                    ? setNavVisible(!navVisible)
                    : setNavCollapsed(!navCollapsed)
                }
                edge="start"
                sx={{
                  marginRight: 3,
                  width: 48,
                  height: 48,
                  ...(navCollapsed && { transform: "rotate(90deg)" }),
                  transition: "transform 0.4s",
                  "&:hover": {
                    transform: navCollapsed ? "rotate(0deg)" : "rotate(90deg)",
                  },
                }}
              >
                <Icon icon="mingcute:menu-fill" />
              </IconButton>
            )}

            <SvgIcon fontSize="large">
              <Logo width={14} height={14} x={0} y={5} />
            </SvgIcon>

            {showLayoutComponents ? (
              <Organization />
            ) : (
              <Typography sx={styles.appName} variant="h6" noWrap>
                Taskforce
              </Typography>
            )}

            {!hidden && (
              <Box
                sx={{
                  position: "absolute",
                  left: "55%",
                  transform: "translateX(-55%)",
                  display: "flex",
                  alignItems: "center",
                }}
              >
                <AutocompleteComponent hidden={hidden} />
              </Box>
            )}

            {showLayoutComponents && (
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  marginLeft: "auto",
                }}
              >
                <NotificationButton />
                <AccountButton />
              </Box>
            )}
          </Toolbar>
        </AppBar>
        {showLayoutComponents && (
          <SideBar
            navCollapsed={navCollapsed}
            setNavCollapsed={setNavCollapsed}
            navVisible={navVisible}
            setNavVisible={setNavVisible}
            hidden={hidden}
          />
        )}
        <Main navCollapsed={navCollapsed} isHidden={hidden}>
          <Offset />
          <Outlet />
        </Main>
      </Box>
    </Suspense>
  );
}

export default Layout;
