import { Route, Switch, useRouteMatch } from "react-router";
import SaleOrder from "../views/sellout/SaleOrder";
export default function SelloutRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={SaleOrder} exact path={`${path}/sale-order`}></Route>
      </Switch>
    </div>
  );
}
