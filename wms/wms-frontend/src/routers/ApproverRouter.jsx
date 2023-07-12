import { Route, Switch, useRouteMatch } from "react-router";
import PresidentReport from "screens/reports/presidentReport";

export default function ApproverRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        
        <Route
          component={PresidentReport}
          exact
          path={`${path}/report`}
        ></Route>
      </Switch>
    </div>
  );
}