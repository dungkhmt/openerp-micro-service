import { Box, CssBaseline, SvgIcon } from "@material-ui/core";
import { MuiThemeProvider, createTheme } from "@material-ui/core/styles";
import Typography from "@mui/material/Typography";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import { request } from "api";
import { FacebookCircularProgress } from "component/common/progressBar/CustomizedCircularProgress.jsx";
import keycloak, { initOptions } from "config/keycloak.js";
import { useEffect, useMemo } from "react";
import { I18nextProvider } from "react-i18next";
import { QueryClient, QueryClientProvider } from "react-query";
import { BrowserRouter } from "react-router-dom";
import { Slide, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { menuState } from "state/MenuState";
import { notificationState } from "state/NotificationState";
import MainRoutes from "./Routes";
import { ReactComponent as HustProgrammingLogo } from "./assets/icons/hust-programming-icon.svg";
import { PLATFORM_NAME } from "./config/config";
// import history from "./history.js";
import i18n from "./translation/i18n";
import { AssignTeacherThesisContext } from "context/AssignTeacherThesisContext";

const theme = createTheme({
  typography: {
    fontFamily: `-apple-system, "Segoe UI", BlinkMacSystemFont, "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif`,
    // fontFamily: `"IBM Plex Sans", -apple-system, "Segoe UI", BlinkMacSystemFont,  "Roboto", "Helvetica Neue", Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol"`,
  },
  overrides: {
    MuiCssBaseline: {
      "@global": {},
    },
  },
});

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    },
  },
});

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
      <SvgIcon
        style={{ fontSize: "150px", marginBottom: "20px" }}
        viewBox="0 0 150 150"
      >
        <HustProgrammingLogo width={140} height={128} x={9} y={9} />
      </SvgIcon>
      <Box>
        <FacebookCircularProgress />
      </Box>
    </Box>
    <Box>
      <Typography sx={{ mb: 4 }}>{PLATFORM_NAME} Team</Typography>
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
    if (event === "onAuthSuccess") {
      request("get", `/`);
    } else if (event === "onAuthError") {
      console.error("Authenticated failed");
    } else if (event === "onAuthLogout") {
      logout();
    } else {
      console.log(event);
    }
  };

  // Fix the bug is described here: https://github.com/facebook/create-react-app/issues/11773
  useEffect(() => {
    window.process = {
      ...window.process,
    };
  }, []);
  const keycloak_ = useMemo(() => keycloak, []);

  return (
    <ReactKeycloakProvider
      authClient={keycloak_}
      initOptions={initOptions}
      LoadingComponent={AppLoading}
      onEvent={onKeycloakEvent}
    >
      <I18nextProvider i18n={i18n}>
        <QueryClientProvider client={queryClient}>
          <MuiThemeProvider theme={theme}>
            <CssBaseline />
            <BrowserRouter>
              <AssignTeacherThesisContext>
                <MainRoutes />
              </AssignTeacherThesisContext>
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
            </BrowserRouter>
          </MuiThemeProvider>
        </QueryClientProvider>
      </I18nextProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
