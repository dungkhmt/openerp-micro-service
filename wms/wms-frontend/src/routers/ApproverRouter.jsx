import { Route, Switch, useRouteMatch } from "react-router";
import ReceiptRequestForApprovalListing from "screens/receipt/receiptRequestForApprovalListing";
import ReceiptRequestForApproval from "screens/receipt/receipRequestForApproval";
import RevuenueReport from "screens/reports/revenueReport";

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
        <Route
          component={ReceiptRequestForApproval}
          exact
          path={`${path}/receipts/:id`}
        ></Route>
        <Route
          component={RevuenueReport}
          exact
          path={`${path}/report`}
        ></Route>
      </Switch>
    </div>
  );
}