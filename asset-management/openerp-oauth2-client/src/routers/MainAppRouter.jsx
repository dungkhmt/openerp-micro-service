import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import { UsersScreen } from "views/UsersScreen";
import { LocationScreen } from "views/settings/LocationScreen";
import { VendorScreen } from "views/settings/VendorScreen";
import { TypeScreen } from "views/settings/TypeScreen";
import AssetsScreen from "views/management/AssetsScreen";
import RequestsScreen from "views/operation/RequestsScreen";

const styles = {
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: "0.5px",
    },
  },
};

function MainAppRouter(props) {
  const location = useLocation();
  const notificationState = useNotificationState();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          {/* <Route component={() => <></>} exact path="/" /> */}
          <PrivateRoute component={UsersScreen} exact path="/" />

          <PrivateRoute component={LocationScreen} exact path="/locations" />

          <PrivateRoute component={VendorScreen} exact path="/vendors" />

          <PrivateRoute component={TypeScreen} exact path="/types" />

          <PrivateRoute component={AssetsScreen} exact path="/assets" />

          <PrivateRoute component={RequestsScreen} path="/requests" />

          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
