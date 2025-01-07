import { Route, Switch, useRouteMatch } from "react-router";
import EmployeeDetails from "../views/EmployeeDetailsScreen";

export default function EmployeeDetailsRouter() {
  let { path } = useRouteMatch();

  return (
    <div>
      <Switch>
        <Route
          component={EmployeeDetails}
          exact
          path={`${path}/:staffCode`}
        ></Route>
      </Switch>
    </div>
  );
}
