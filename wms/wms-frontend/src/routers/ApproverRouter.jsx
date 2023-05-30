import { Route, Switch, useRouteMatch } from "react-router";
import ReceiptRequestForApprovalListing from "screens/receipt/receiptRequestForApprovalListing";
import ReceiptRequestForApproval from "screens/receipt/receipRequestForApproval";
import PresidentReport from "screens/reports/presidentReport";

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
          component={PresidentReport}
          exact
          path={`${path}/report`}
        ></Route>
      </Switch>
    </div>
  );
}