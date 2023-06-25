import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import TestComponent from "../test/TestComponent";
import AllUsers from "../views/general/AllUsers";
import DashBoard from "../views/general/Dashboard";
import CategoryRouter from "./CategoryRouter";
import DeliveryRouter from "./DeliveryRouter";
import PrivateRoute from "./PrivateRoute";
import SellinRouter from "./SellinRouter";
import SelloutRouter from "./SelloutRouter";
import WarehouseRouter from "./WarehouseRouter";

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
          <Route component={DashBoard} exact path="/" />
          <Route component={TestComponent} exact path="/test" />
          <Route component={CategoryRouter} path="/category" />
          <PrivateRoute component={SellinRouter} path="/sellin" />
          <PrivateRoute component={SelloutRouter} path="/sellout" />
          <PrivateRoute component={WarehouseRouter} path="/warehouse" />
          <PrivateRoute component={DeliveryRouter} path="/delivery" />
          <Route component={AllUsers} path="/all-user" />
          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
