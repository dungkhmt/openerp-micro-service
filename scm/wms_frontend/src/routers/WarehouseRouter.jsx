import { Route, Switch, useRouteMatch } from "react-router";
import OrderDetail from "../views/sellin/PurchaseOrderDetail";
import Warehouse from "../views/warehouse/Facility";
import ImportingActivity from "../views/warehouse/ImportingActivity";

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
        <Route
          component={OrderDetail}
          exact
          path={`${path}/importing/order-detail`}
        ></Route>
      </Switch>
    </div>
  );
}
