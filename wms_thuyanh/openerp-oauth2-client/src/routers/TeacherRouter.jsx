import ProtectedScreen from "components/ProtectedScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function TeacherRouter() {
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
