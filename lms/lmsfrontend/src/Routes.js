import {LinearProgress} from "@material-ui/core";
import React, {lazy, Suspense} from "react";
import {Route, Switch} from "react-router-dom";
import PrivateRoute from "./common/PrivateRoute";
import {Layout} from "./layout";
import MainAppRoute from "./routers/MainAppRoutes";
import {routeState} from "./state/RouteState";

// const Register = lazy(() => import("../src/views/UserRegister/Register"));
const ForgetPassword = lazy(() =>
  import("../src/views/UserRegister/ForgetPassword")
);

// const MainAppRoute = lazy(() => import("./routers/MainAppRoutes"));
const ChangePassword = lazy(() =>
  import("./component/userlogin/changepassword")
);

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
        {/*<Route component={Register} layout={Layout} path="/user/register" />*/}
        <Route
          component={ForgetPassword}
          layout={Layout}
          path="/user/forgetpassword"
        />
        <PrivateRoute
          component={ChangePassword}
          path="/userlogin/change-password/:username"
        />
        <Route
          path="*"
          render={(props) => {
            routeState.merge({
              currentRoute: props.location.pathname,
            });

            return <MainAppRoute {...props} />;
          }}
        />
      </Switch>
    </Suspense>
  );
}

export default Routes;
