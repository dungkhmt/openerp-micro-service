import ProtectedScreen from "components/ProtectedScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function EmployeeRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ProtectedScreen}
          exact
          path={`${path}/department`}
        ></Route>
      </Switch>
    </div>
  );
}
