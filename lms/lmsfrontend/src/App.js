import {CssBaseline} from "@material-ui/core";
import {createTheme, MuiThemeProvider} from "@material-ui/core/styles";
import {useEffect} from "react";
import {I18nextProvider} from "react-i18next";
import {QueryClient, QueryClientProvider} from "react-query";
import {Router} from "react-router-dom";
import {Slide, ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import history from "./history.js";
import Routes from "./Routes";
import {ReactKeycloakProvider} from "@react-keycloak/web";
import i18n from "./translation/i18n";
import keycloak, {initOptions} from "./config/keycloak";
import {request} from "./api";
import {menuState} from "./state/MenuState";
import {notificationState} from "./state/NotificationState";
import { ReactComponent as Logo } from "./assets/icons/logo.svg";
import {Box, CircularProgress, SvgIcon, Typography} from "@material-ui/core";

const theme = createTheme({
  typography: {
    fontFamily: `-apple-system, "Segoe UI", BlinkMacSystemFont, "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif`,
  },
  overrides: {
    MuiCssBaseline: {
      "@global": {
      },
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
      <SvgIcon sx={{ fontSize: 150, mb: 4 }} viewBox="0 0 150 150">
        <Logo width={132} height={132} x={9} y={9} />
      </SvgIcon>
      <CircularProgress/>
    </Box>
    <Box>
      <Typography sx={{ mb: 4 }}>OpenERP Team</Typography>
    </Box>
  </Box>
);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      keepPreviousData: true,
    },
  },
});

function App() {

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
      <I18nextProvider i18n={i18n}>
        <QueryClientProvider client={queryClient}>
          <MuiThemeProvider theme={theme}>
            <CssBaseline/>
            {/* <Router> */}
            <Router history={history}>
              <Routes/>
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
            {/* </Router> */}
          </MuiThemeProvider>
        </QueryClientProvider>
      </I18nextProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
