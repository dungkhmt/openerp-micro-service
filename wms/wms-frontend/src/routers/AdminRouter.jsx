import ReceiptRequestProcess from "screens/receipt/receiptRequestProcess";
import ReceiptRequestProcessListing from "screens/receipt/receiptRequestProcessListing";
import CreateWarehouse from "screens/warehouse/createWarehouse";
import { Route, Switch, useRouteMatch } from "react-router";
import ProductListing from "screens/product/productListing";
import ProductDetail from "screens/product/productDetail";
import ListWarehouse from "screens/warehouse/listWarehouses";
import AdminOrderListing from "screens/order/orderListing";
import AdminOrderDetail from "screens/order/adminOrderDetail";
import ReceiptBilLDetail from "screens/receipt/receiptBillDetail";
import DeliveryBillDetail from "screens/shipment/deliveryBillDetails";

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
        <Route
          component={AdminOrderDetail}
          exact
          path={`${path}/orders/:id`}
        ></Route>
        <Route
          component={ReceiptBilLDetail}
          exact
          path={`${path}/receipt-bill/:id`}
        ></Route>
        <Route
          component={DeliveryBillDetail}
          exact
          path={`${path}/delivery-bill/:id`}
        ></Route>
      </Switch>
    </div>
  );
}