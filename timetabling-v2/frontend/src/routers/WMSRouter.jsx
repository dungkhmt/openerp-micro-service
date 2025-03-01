import { Route, Switch, useRouteMatch } from "react-router";
import PurchaseOrder from "views/wms/purchaseorder";

export default function WMSRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={PurchaseOrder}
          exact
          path={`${path}/purchase/orders`}
        >
        </Route>
        
      </Switch>
    </div>
  );
}
