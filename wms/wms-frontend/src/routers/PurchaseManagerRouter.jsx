import { Route, Switch, useRouteMatch } from "react-router";
import ReceiptRequestForApproval from "screens/receipt/receipRequestForApproval";
import ReceiptRequestForApprovalListing from "screens/receipt/receiptRequestForApprovalListing";

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
      </Switch>
    </div>
  );
}