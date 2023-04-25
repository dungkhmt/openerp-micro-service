import { Route, Switch, useRouteMatch } from "react-router";
import Shipment from "../views/delivery/Shipment";
import ImportingActivity from "../views/warehouse/ImportingActivity";

export default function DeliveryRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Shipment} exact path={`${path}/shipment`}></Route>
        <Route
          component={ImportingActivity}
          exact
          path={`${path}/importing`}
        ></Route>
      </Switch>
    </div>
  );
}
