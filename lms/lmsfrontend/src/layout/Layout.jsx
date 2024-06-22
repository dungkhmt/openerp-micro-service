import AppBar from "@material-ui/core/AppBar";
import Typography from "@material-ui/core/Typography";
import { makeStyles } from "@material-ui/core/styles";
import { default as MenuIcon } from "@mui/icons-material/Menu";
import { Box, IconButton, SvgIcon, Toolbar } from "@mui/material";
import { styled } from "@mui/material/styles";
import { useKeycloak } from "@react-keycloak/web";
import { useState } from "react";
import { ReactComponent as HustProgrammingLogo } from "../assets/icons/hust-programming-icon.svg";
import bgImage from "../assets/img/sidebar1.jpg";
import { PLATFORM_NAME } from "../config/config";
import AccountButton from "./account/AccountButton";
import LanguageSwitch from "./languageswitcher/LanguageSwitch";
import NotificationButton from "./notification/NotificationButton";
import SideBar, { drawerWidth } from "./sidebar/v1/SideBar";

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

const Main = styled("main", { shouldForwardProp: (prop) => prop !== "open" })(
  ({ theme, open }) => ({
    flexGrow: 1,
    width: "100%",
    padding: theme.spacing(3),
    transition: theme.transitions.create("padding", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: `-${drawerWidth}px`,
    ...(open && {
      transition: theme.transitions.create("padding", {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
      paddingLeft: `${drawerWidth + parseInt(theme.spacing(3).slice(0, -2))}px`,
    }),
  })
);

const useStyles = makeStyles((theme) => ({
  appBar: {
    // position: "sticky", // sticky is not supported by IE11.
    top: 0,
    transition: theme.transitions.create("top"),
    backdropFilter: "blur(20px)",
    boxShadow: `inset 0px -1px 1px ${theme.palette.grey[100]}`,
    backgroundColor: "rgba(255,255,255,0.72)",
    zIndex: theme.zIndex.drawer + 1,
  },
  sectionDesktop: {
    display: "none",
    [theme.breakpoints.up("md")]: {
      display: "flex",
      alignItems: "center",
    },
  },
  appName: {
    paddingLeft: 8,
    color: "#aa1d2b",
    display: "none",
    [theme.breakpoints.up("sm")]: {
      display: "block",
    },
  },
}));

function Layout({ children }) {
  const classes = useStyles();

  const { keycloak } = useKeycloak();
  const [open, setOpen] = useState(true);
  const [image] = useState(bgImage);
  const [color] = useState("blue");

  return (
    <Box sx={{ display: "flex" }}>
      <AppBar position="fixed" color="inherit" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            sx={{ mr: 3 }}
            onClick={() => setOpen(!open)}
          >
            <MenuIcon />
          </IconButton>
          <SvgIcon fontSize="large">
            <HustProgrammingLogo width={22} height={19} x={2} y={2} />
          </SvgIcon>

          <Typography className={classes.appName} variant="h6" noWrap>
            {PLATFORM_NAME}
          </Typography>

          {/* Use this div tag to push the icons to the right */}
          <Box sx={{ flexGrow: 1 }} />
          <div className={classes.sectionDesktop}>
            <LanguageSwitch />
            {keycloak.authenticated && (
              <>
                <NotificationButton />
                <AccountButton />
              </>
            )}
          </div>
        </Toolbar>
      </AppBar>
      <SideBar open={open} image={image} color={color} />
      <Main open={open}>
        <Offset />
        {children}
      </Main>
    </Box>
  );
}

export default Layout;
