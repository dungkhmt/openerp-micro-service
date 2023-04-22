import { Route, Switch, useRouteMatch } from "react-router";
import ImportingActivity from "../views/warehouse/ImportingActivity";
import Warehouse from "../views/warehouse/Warehouse";

export default function WarehouseRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Warehouse} exact path={`${path}/inventory`}></Route>
        <Route
          component={ImportingActivity}
          exact
          path={`${path}/importing`}
        ></Route>
      </Switch>
    </div>
  );
}
