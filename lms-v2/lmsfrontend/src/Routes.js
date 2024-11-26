import {LinearProgress} from "@material-ui/core";
import React, {Suspense} from "react";
import {Route, Switch} from "react-router-dom";
import MainAppRoute from "./routers/MainAppRoutes";
import {routeState} from "./state/RouteState";
import {Home} from "./component";

// const Register = lazy(() => import("../src/views/UserRegister/Register"));

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
        {/*<Route component={Home} exact path="/" />*/}
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
