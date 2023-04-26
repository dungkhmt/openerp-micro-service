import { Route, Switch, useRouteMatch } from "react-router";
import Shipment from "../views/delivery/Shipment";
import ShipmentDetail from "../views/delivery/ShipmentDetail";
import SplitBillDetail from "../views/delivery/SplitBillDetail";
import SplitOrder from "../views/delivery/SplitOrder";

export default function DeliveryRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Shipment} exact path={`${path}/shipment`}></Route>
        <Route
          component={ShipmentDetail}
          exact
          path={`${path}/shipment/shipment-detail`}
        ></Route>
        <Route
          component={SplitOrder}
          exact
          path={`${path}/split-order`}
        ></Route>
        <Route
          component={SplitBillDetail}
          exact
          path={`${path}/split-order/split-bill-detail`}
        ></Route>
      </Switch>
    </div>
  );
}
