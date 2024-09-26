import { Route, Switch, useRouteMatch } from "react-router";
import ReceiptRequestForApproval from "screens/receipt/receipRequestForApproval";
import ReceiptRequestForApprovalListing from "screens/receipt/receiptRequestForApprovalListing";
import receiptRequestDetailForPurchaseManager from "screens/receipt/receiptRequestDetailForPurchaseManager";

export default function PurchaseManagerRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ReceiptRequestForApprovalListing}
          exact
          path={`${path}/receipts`}
        ></Route>
        <Route
          component={ReceiptRequestForApproval}
          exact
          path={`${path}/receipts/:id`}
        ></Route>
        <Route
          component={receiptRequestDetailForPurchaseManager} 
          exact
          path={`${path}/create-receipt`}
        ></Route>
      </Switch>
    </div>
  );
}