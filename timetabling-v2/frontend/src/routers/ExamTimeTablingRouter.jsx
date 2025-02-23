import ClassListOpenedScreen from "views/exam-timetabling/classListOpened/ExamClassListScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function ExamTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ClassListOpenedScreen}
          exact
          path={`${path}/class-list`}
        ></Route>
      </Switch>
    </div>
  );
}
