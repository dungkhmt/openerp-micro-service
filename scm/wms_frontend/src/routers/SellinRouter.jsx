import { Route, Switch, useRouteMatch } from "react-router";
import PurchaseOrder from "../views/sellin/PurchaseOrder";

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
      </Switch>
    </div>
  );
}
