import { Route, Switch, useRouteMatch } from "react-router";
import PriceConfig from "screens/product/priceConfig";
import ReceiptRequestDetail from "screens/receipt/receiptRequestDetail";
import SaleManagerOrderListing from "screens/order/saleManagerOrderListing";
import OrderApprovalDetail from "screens/order/orderApprovalDetail";

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
        <Route
          component={SaleManagerOrderListing}
          exact
          path={`${path}/orders`}
        ></Route>
        <Route
          component={OrderApprovalDetail}
          exact
          path={`${path}/orders/:id`}
        ></Route>
      </Switch>
    </div>
  );
}