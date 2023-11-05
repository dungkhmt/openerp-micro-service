import { ReactKeycloakProvider } from "@react-keycloak/web";
import { request } from "./api";
import i18n from "./translation/i18n";
import history from "./history";
import keycloak, { initOptions } from "./config/keycloak.js";
import { BrowserRouter } from "react-router-dom";
import { Slide, ToastContainer } from "react-toastify";
import { AppLoading } from "./components/common/AppLoading";
import Routes from "./Routes";
import { CssBaseline, createTheme, ThemeProvider } from "@mui/material";
import { I18nextProvider } from "react-i18next";
import { menuState } from "./state/MenuState";
import { notificationState } from "./state/NotificationState";
import { useEffect, useMemo } from "react";

const theme = createTheme({
  typography: {
    fontFamily: `-apple-system, "Segoe UI", BlinkMacSystemFont, "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif`,
  },
  overrides: {
    MuiCssBaseline: {
      "@global": {},
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

  const onKeycloakEvent = async (event) => {
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
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <BrowserRouter history={history}>
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
          </BrowserRouter>
        </ThemeProvider>
      </I18nextProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
