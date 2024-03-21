import createCache from "@emotion/cache";
import { CacheProvider } from "@emotion/react";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import { useEffect } from "react";
import { Toaster } from "react-hot-toast";
import { I18nextProvider } from "react-i18next";
import { BrowserRouter } from "react-router-dom";
import Routes from "./Routes";
import { AppLoading } from "./components/common/AppLoading";
import { ReactHotToast } from "./components/react-hot-toast/index.jsx";
import keycloak, { initOptions } from "./config/keycloak.js";
import history from "./history";
import { UserService } from "./services/api/user.service.js";
import { menuState } from "./state/MenuState";
import { notificationState } from "./state/NotificationState";
import ThemeProvider from "./theme/ThemProvider.jsx";
import i18n from "./translation/i18n";

function App() {
  const clientSideEmotionCache = createCache({ key: "css" });
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
      UserService.sync();
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

  return (
    <ReactKeycloakProvider
      authClient={keycloak}
      initOptions={initOptions}
      LoadingComponent={AppLoading}
      onEvent={onKeycloakEvent}
    >
      <I18nextProvider i18n={i18n}>
        <CacheProvider value={clientSideEmotionCache}>
          <ThemeProvider>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <ReactHotToast>
                <Toaster
                  position="top-right"
                  toastOptions={{
                    className: "react-hot-toast",
                    duration: 5000,
                  }}
                />
              </ReactHotToast>
              <BrowserRouter history={history}>
                <Routes />
              </BrowserRouter>
            </LocalizationProvider>
          </ThemeProvider>
        </CacheProvider>
      </I18nextProvider>
    </ReactKeycloakProvider>
  );
}

export default App;
