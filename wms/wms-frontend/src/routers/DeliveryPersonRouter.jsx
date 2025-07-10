import { Route, Switch, useRouteMatch } from "react-router";
import TodayDeliveryTrip from "screens/deliveryperson/todayDeliveryTrip";
import DeliveryPersonTripDetail from "screens/deliveryperson/deliveryPersonTripDetail";

export default function DeliveryPersonRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={TodayDeliveryTrip}
          exact
          path={`${path}/trip`}
        ></Route>
        <Route
          component={DeliveryPersonTripDetail}
          exact
          path={`${path}/trip/:id`}
        ></Route>
      </Switch>
    </div>
  );
}