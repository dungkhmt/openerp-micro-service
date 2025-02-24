import ExamClassScreen from "views/exam-timetabling/classListOpened/ExamClassListScreen";
import { Route, Switch, useRouteMatch } from "react-router";
import ExamPlanListPage from "views/exam-timetabling/examPlan/ExamPlanScreen"

export default function ExamTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ExamClassScreen}
          exact
          path={`${path}/class-list`}
        ></Route>
        <Route
          component={ExamPlanListPage}
          exact
          path={`${path}/exam-schedule`}
        ></Route>
      </Switch>
    </div>
  );
}
