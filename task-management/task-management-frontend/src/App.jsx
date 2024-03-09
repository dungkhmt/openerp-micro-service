import { CssBaseline, ThemeProvider, createTheme } from "@mui/material";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import { useEffect, useMemo } from "react";
import { Toaster } from "react-hot-toast";
import { I18nextProvider } from "react-i18next";
import { BrowserRouter } from "react-router-dom";
import Routes from "./Routes";
import { request } from "./api";
import { AppLoading } from "./components/common/AppLoading";
import keycloak, { initOptions } from "./config/keycloak.js";
import history from "./history";
import { menuState } from "./state/MenuState";
import { notificationState } from "./state/NotificationState";
import i18n from "./translation/i18n";

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
          <Toaster duration={5000} />
          <BrowserRouter history={history}>
            <Routes />
          </BrowserRouter>
        </ThemeProvider>
      </I18nextProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
