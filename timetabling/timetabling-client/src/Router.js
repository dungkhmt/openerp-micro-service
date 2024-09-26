import { LinearProgress } from "@mui/material";
import { Suspense } from "react";
import { Route, Switch } from "react-router-dom";
import MainAppRouter from "./routers/MainAppRouter";
import { routeState } from "./state/RouteState";

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
        <Route
          path="*"
          render={(props) => {
            routeState.merge({
              currentRoute: props.location.pathname,
            });

            return <MainAppRouter {...props} />;
          }}
        />
      </Switch>
    </Suspense>
  );
}

export default Routes;
