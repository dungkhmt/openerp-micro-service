import { Route, Switch, useRouteMatch } from "react-router";
import SaleOrder from "../views/sellout/SaleOrder";
import SaleOrderDetail from "../views/sellout/SaleOrderDetail";
import SalePrice from "../views/sellout/SalePrice";
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
        <Route component={SalePrice} exact path={`${path}/sale-price`}></Route>
      </Switch>
    </div>
  );
}
