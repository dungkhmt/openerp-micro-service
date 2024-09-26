import { CssBaseline, SvgIcon, Typography } from "@mui/material";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import { Box } from "@mui/system";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import Routes from "Router";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress.jsx";
import keycloak, { initOptions } from "config/keycloak.js";
import { useEffect } from "react";
import { Router } from "react-router-dom";
import { Slide, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { menuState } from "state/MenuState";
import { notificationState } from "state/NotificationState";
import { ReactComponent as Logo } from "./assets/icons/logo.svg";
import history from "./history.js";

export const theme = createTheme({
  typography: {
    fontFamily: `-apple-system, "Segoe UI", BlinkMacSystemFont, "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif`,
  },
  overrides: {
    MuiCssBaseline: {
      "@global": {
        // "*, *::before, *::after": {
        //   boxSizing: "content-box",
        // },
        // body: {
        //   height: "100%",
        //   backgroundColor: "#fff",
        // },
      },
    },
  },
});

console.log(
  "%c\n\n \u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2557  \u2588\u2588\u2557\u2003\u2003\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557\n\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255D\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2551\u2003\u2003\u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255D\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\n\u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255D\u2588\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2554\u2588\u2588\u2557\u2588\u2588\u2551\u2003\u2003\u2588\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255D\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255D\n\u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u255D \u2588\u2588\u2554\u2550\u2550\u255D  \u2588\u2588\u2551\u255A\u2588\u2588\u2588\u2588\u2551\u2003\u2003\u2588\u2588\u2554\u2550\u2550\u255D  \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2550\u255D\n\u255A\u2588\u2588\u2588\u2588\u2588\u2554\u255D\u2588\u2588\u2551     \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2551 \u255A\u2588\u2588\u2588\u2551\u2003\u2003\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2551\n \u255A\u2550\u2550\u2550\u2550\u255D \u255A\u2550\u255D     \u255A\u2550\u2550\u2550\u2550\u2550\u2550\u255D\u255A\u2550\u255D  \u255A\u2550\u2550\u255D\u2003\u2003\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u255D\u255A\u2550\u255D  \u255A\u2550\u255D\u255A\u2550\u255D\n\n",
  "font-family:monospace;color:#1976d2;font-size:12px;"
);

const AppLoading = (
  <Box
    sx={{
      height: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      flexDirection: "column",
    }}
  >
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
        flexGrow: 1,
      }}
    >
      <SvgIcon sx={{ fontSize: 150, mb: 4 }} viewBox="0 0 150 150">
        <Logo width={132} height={132} x={9} y={9} />
      </SvgIcon>
      <Box>
        <FacebookCircularProgress />
      </Box>
    </Box>
    <Box>
      <Typography sx={{ mb: 4 }}>OpenERP Team</Typography>
    </Box>
  </Box>
);

function App() {
  // TODO: Consider remove this logic!
  const logout = () => {
    menuState.permittedFunctions.set(new Set());
    notificationState.merge({
      notifications: undefined,
      numUnRead: 0,
      hasMore: false,
    });
  };

  const onKeycloakEvent = async (event, error) => {
    console.debug(event);
    if (event === "onAuthSuccess") {
      request("get", `/user`);
    } else if (event === "onAuthError") {
      console.error("Authenticated failed");
    } else if (event === "onAuthLogout") {
      logout();
    }
  };

  // Fix the bug is described here: https://github.com/facebook/create-react-app/issues/11773
  useEffect(() => {
    window.process = {
      ...window.process,
    };
  }, []);

  return (
    <ReactKeycloakProvider
      authClient={keycloak}
      initOptions={initOptions}
      LoadingComponent={AppLoading}
      onEvent={onKeycloakEvent}
    >
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Router history={history}>
          <Routes />
          <ToastContainer
            position="bottom-center"
            transition={Slide}
            autoClose={3000}
            limit={3}
            hideProgressBar={true}
            newestOnTop
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
          />
        </Router>
      </ThemeProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
