import { Route, Switch, useRouteMatch } from "react-router";
import PriceConfig from "screens/product/priceConfig";
import ReceiptRequestDetail from "screens/receipt/receiptRequestDetail";

export default function SaleManagerRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={PriceConfig}
          exact
          path={`${path}/price-config`}
        ></Route>
        <Route
          component={ReceiptRequestDetail}
          exact
          path={`${path}/receipt-request`}
        ></Route>
      </Switch>
    </div>
  );
}