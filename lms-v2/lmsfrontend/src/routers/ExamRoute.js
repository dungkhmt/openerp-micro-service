import { Route, Switch, useRouteMatch } from "react-router";
import QuestionBank from "../component/education/exam/questionbank/QuestionBank";
import QuestionBankCreateUpdate from "../component/education/exam/questionbank/QuestionBankCreateUpdate";

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
      </Switch>
    </div>
  );
}
