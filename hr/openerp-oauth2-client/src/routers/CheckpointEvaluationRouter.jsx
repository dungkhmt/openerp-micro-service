import { Route, Switch, useRouteMatch } from "react-router";
import CheckpointEvaluation from "../views/CheckpointEvaluationScreen";

export default function CheckpointEvaluationRouter() {
  let { path } = useRouteMatch();

  return (
    <div>
      <Switch>
        <Route
          component={CheckpointEvaluation}
          exact
          path={`${path}`}
        ></Route>
      </Switch>
    </div>
  );
}
