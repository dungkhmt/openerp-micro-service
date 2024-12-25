import { Route, Switch, useRouteMatch } from "react-router";
import QuestionBank from "../component/education/exam/questionbank/QuestionBank";

export default function ExamRoute() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={QuestionBank}
          exact
          path={`${path}/question-bank`}
        ></Route>
      </Switch>
    </div>
  );
}
