import { Route, Switch, useRouteMatch } from "react-router";
import DeliveryPersonManagement from "screens/deliveryperson/deliveryPersonManagement";
import ShipmentListing from "screens/shipment/shipmentListing";
import ShipmentDetail from "screens/shipment/shipmentDetail";
import DeliveryTripListing from "screens/shipment/deliveryTripListing";
import DeliveryTripDetail from "screens/shipment/deliveryTripDetail";

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
        <Route
          component={ShipmentListing}
          exact
          path={`${path}/shipments`}
        ></Route>
        <Route
          component={ShipmentDetail}
          exact
          path={`${path}/shipments/:id`}
        ></Route>
        <Route
          component={DeliveryTripListing}
          exact
          path={`${path}/delivery-trips`}
        ></Route>
        <Route
          component={DeliveryTripDetail}
          exact
          path={`${path}/delivery-trips/:id`}
        ></Route>
      </Switch>
    </div>
  );
}