import { Route, Switch, useRouteMatch } from "react-router";
import QuestionBank from "../component/education/exam/questionbank/QuestionBank";
import QuestionBankCreateUpdate from "../component/education/exam/questionbank/QuestionBankCreateUpdate";
import TestBank from "../component/education/exam/testbank/TestBank";
import TestBankCreateUpdate from "../component/education/exam/testbank/TestBankCreateUpdate";
import ExamManagement from "../component/education/exam/exammanagement/ExamManagement";
import ExamCreateUpdate from "../component/education/exam/exammanagement/ExamCreateUpdate";
import MyExam from "../component/education/exam/myexam/MyExam";
import MyExamDetails from "../component/education/exam/myexam/MyExamDetails";
import ExamSubjectManagement from "../component/education/exam/subject/ExamSubjectManagement";
import ExamSubjectCreateUpdate from "../component/education/exam/subject/ExamSubjectCreateUpdate";

export default function ExamRoute() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ExamSubjectManagement}
          exact
          path={`${path}/subject`}
        ></Route>
        <Route
          component={ExamSubjectCreateUpdate}
          exact
          path={`${path}/create-update-subject`}
        ></Route>

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
        <Route
          component={TestBankCreateUpdate}
          exact
          path={`${path}/create-update-test-bank`}
        ></Route>

        <Route
          component={ExamManagement}
          exact
          path={`${path}/management`}
        ></Route>
        <Route
          component={ExamCreateUpdate}
          exact
          path={`${path}/create-update`}
        ></Route>

        <Route
          component={MyExam}
          exact
          path={`${path}/my-exam`}
        ></Route>
        <Route
          component={MyExamDetails}
          exact
          path={`${path}/doing`}
        ></Route>
      </Switch>
    </div>
  );
}
