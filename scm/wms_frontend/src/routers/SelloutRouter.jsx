import { Route, Switch, useRouteMatch } from "react-router";
import SaleOrder from "../views/sellout/SaleOrder";
import SaleOrderDetail from "../views/sellout/SaleOrderDetail";
export default function SelloutRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={SaleOrder} exact path={`${path}/sale-order`}></Route>
        <Route
          component={SaleOrderDetail}
          exact
          path={`${path}/sale-order/sale-order-detail`}
        ></Route>
      </Switch>
    </div>
  );
}
