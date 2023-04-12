import { Route, Switch, useRouteMatch } from "react-router";
import ReceiptRequestForApprovalListing from "screens/receipt/receiptRequestForApprovalListing";
import ReceiptRequestForApproval from "screens/receipt/receipRequestForApproval";

export default function ApproverRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ReceiptRequestForApprovalListing}
          exact
          path={`${path}/receipts`}
        ></Route>
      </Switch>
    </div>
  );
}