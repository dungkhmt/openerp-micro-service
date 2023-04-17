import { Route, Switch, useRouteMatch } from "react-router";
import DeliveryPersonManagement from "screens/deliveryperson/deliveryPersonManagement";

export default function DeliveryManagerRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={DeliveryPersonManagement}
          exact
          path={`${path}/delivery-person`}
        ></Route>
      </Switch>
    </div>
  );
}