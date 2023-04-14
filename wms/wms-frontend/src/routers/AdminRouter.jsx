import ReceiptRequestProcess from "screens/receipt/receiptRequestProcess";
import ReceiptRequestProcessListing from "screens/receipt/receiptRequestProcessListing";
import CreateWarehouse from "screens/warehouse/createWarehouse";
import { Route, Switch, useRouteMatch } from "react-router";
import ProductListing from "screens/product/productListing";
import ProductDetail from "screens/product/productDetail";
import ListWarehouse from "screens/warehouse/listWarehouses";
import AdminOrderListing from "screens/order/orderListing";

export default function AdminRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ListWarehouse}
          exact
          path={`${path}/warehouse`}
        ></Route>
        <Route
          component={CreateWarehouse}
          exact
          path={`${path}/warehouse/create`}
        ></Route>
        <Route
          component={CreateWarehouse}
          exact
          path={`${path}/warehouse/update/:id`}
        ></Route>
        <Route
          component={ProductListing}
          exact
          path={`${path}/product`}
        ></Route>
        <Route
          component={ProductDetail}
          exact
          path={`${path}/product/create`}
        ></Route>
        <Route
          component={ProductDetail}
          exact
          path={`${path}/product/update/:id`}
        ></Route>
        <Route
          component={AdminOrderListing}
          exact
          path={`${path}/orders`}
        ></Route>
        <Route
          component={ReceiptRequestProcessListing}
          exact
          path={`${path}/process-receipts`}
        ></Route>
        <Route
          component={ReceiptRequestProcess}
          exact
          path={`${path}/process-receipts/:id`}
        ></Route>
      </Switch>
    </div>
  );
}