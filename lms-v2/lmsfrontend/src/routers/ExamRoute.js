import { Route, Switch, useRouteMatch } from "react-router";
import QuestionBank from "../component/education/exam/questionbank/QuestionBank";
import QuestionBankCreateUpdate from "../component/education/exam/questionbank/QuestionBankCreateUpdate";
import TestBank from "../component/education/exam/testbank/TestBank";

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
        <Route
          component={QuestionBankCreateUpdate}
          exact
          path={`${path}/create-update-question-bank`}
        ></Route>
        <Route
          component={TestBank}
          exact
          path={`${path}/test-bank`}
        ></Route>
      </Switch>
    </div>
  );
}
