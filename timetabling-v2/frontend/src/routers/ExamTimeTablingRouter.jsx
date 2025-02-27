import ExamClassScreen from "views/exam-timetabling/classListOpened/ExamClassListScreen";
import { Route, Switch, useRouteMatch } from "react-router";
import ExamPlanListPage from "views/exam-timetabling/examPlan/ExamPlanScreen"
import ExamPlanDetailPage from "views/exam-timetabling/examPlan/ExamPlanDetailScreen"

export default function ExamTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
         component={ExamClassScreen}
         exact
         path={`${path}/class-list/:planId`}
        ></Route>
        <Route
          component={ExamClassScreen}
          exact
          path={`${path}/class-list`}
        ></Route>
        <Route
          component={ExamPlanListPage}
          exact
          path={`${path}/exam-plan`}
        ></Route>
        <Route
          component={ExamPlanDetailPage}
          path={`${path}/exam-plan/:id`}
          exact
        ></Route>
      </Switch>
    </div>
  );
}
