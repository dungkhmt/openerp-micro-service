import { Route, Switch, useRouteMatch } from "react-router";
import PurchaseOrder from "../views/sellin/PurchaseOrder";
import PurchaseOrderDetail from "../views/sellin/PurchaseOrderDetail";
import PurchasePrice from "../views/sellin/PurchasePrice";
import PurchaseStaff from "../views/sellin/PurchaseStaff";

export default function SellinRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={PurchaseOrder}
          exact
          path={`${path}/purchase-order`}
        ></Route>
        <Route
          component={PurchaseOrderDetail}
          exact
          path={`${path}/purchase-order/purchase-order-detail`}
        ></Route>
        <Route
          component={PurchasePrice}
          exact
          path={`${path}/purchase-price`}
        ></Route>
        <Route
          component={PurchaseStaff}
          exact
          path={`${path}/purchase-staff`}
        ></Route>
      </Switch>
    </div>
  );
}
