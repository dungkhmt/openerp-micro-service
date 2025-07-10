import { LinearProgress } from "@mui/material";
import { Suspense } from "react";
import { Route, Switch } from "react-router-dom";
import MainAppRouter from "./routers/MainAppRouter";
import { routeState } from "./state/RouteState";

// const Register = lazy(() => import("../src/views/UserRegister/Register"));

// Import new components
import DeliveryTracking from './screens/order/DeliveryTracking';
import DeliveryAnalytics from './screens/analytics/DeliveryAnalytics';
import RouteOptimization from './screens/order/RouteOptimization';

function Routes(props) {
  return (
    <Suspense
      fallback={
        <LinearProgress
          style={{
            position: "absolute",
            top: 0,
            width: "100%",
            zIndex: 1202,
          }}
        />
      }
    >
      <Switch>
        {/* <Route component={Register} layout={Layout} path="/user/register" /> */}
        <Route
          path="*"
          render={(props) => {
            routeState.merge({
              currentRoute: props.location.pathname,
            });

            return <MainAppRouter {...props} />;
          }}
        />
        {/* Add new routes in the router configuration */}
        <Route
          path="/delivery/tracking/:orderId"
          element={<DeliveryTracking />}
        />
        <Route
          path="/delivery/analytics"
          element={<DeliveryAnalytics />}
        />
        <Route
          path="/delivery/route/:shipperId"
          element={<RouteOptimization />}
        />
      </Switch>
    </Suspense>
  );
}

export default Routes;
