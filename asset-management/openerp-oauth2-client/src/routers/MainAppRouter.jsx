import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import { MyAssetScreen } from "views/home/MyAssetScreen";
import { LocationScreen } from "views/settings/LocationScreen";
import { VendorScreen } from "views/settings/VendorScreen";
import { TypeScreen } from "views/settings/TypeScreen";
import AssetsScreen from "views/management/AssetsScreen";
import RequestsScreen from "views/operation/RequestsScreen";
import RequestDetail from "views/operation/request/RequestDetail";
import AssetReport from "views/reports/AssetReport";
import RequestReport from "views/reports/RequestReport";
import AssetDetail from "views/management/AssetDetail";
import MyRequestScreen from "views/home/MyRequestScreen";
import LocationDetail from "views/settings/LocationDetail";

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
          <PrivateRoute component={MyAssetScreen} exact path="/" />

          <PrivateRoute component={MyRequestScreen} exact path="/my-request" />

          <PrivateRoute component={LocationScreen} exact path="/locations" />

          <PrivateRoute component={LocationDetail} exact path="/location/:id" />

          <PrivateRoute component={VendorScreen} exact path="/vendors" />

          <PrivateRoute component={TypeScreen} exact path="/types" />

          <PrivateRoute component={AssetsScreen} exact path="/assets" />

          <PrivateRoute component={AssetDetail} path="/asset/:id" />

          <PrivateRoute component={RequestsScreen} exact path="/requests" />

          <PrivateRoute component={RequestDetail} path="/request/:id" />

          <PrivateRoute component={AssetReport} path="/reports/asset"/>

          <PrivateRoute component={RequestReport} path="/reports/request"/>

          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
