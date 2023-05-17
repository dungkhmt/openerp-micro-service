import { Route, Switch, useRouteMatch } from "react-router";
import PurchaseOrder from "../views/sellin/PurchaseOrder";
import PurchaseOrderDetail from "../views/sellin/PurchaseOrderDetail";
import PurchasePrice from "../views/sellin/PurchasePrice";

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
      </Switch>
    </div>
  );
}
