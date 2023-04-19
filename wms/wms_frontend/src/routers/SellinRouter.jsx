import { Route, Switch, useRouteMatch } from "react-router";
import ProtectedScreen from "../views/ProtectedScreen";

export default function SellinRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ProtectedScreen}
          exact
          path={`${path}/screen-1`}
        ></Route>
      </Switch>
    </div>
  );
}
