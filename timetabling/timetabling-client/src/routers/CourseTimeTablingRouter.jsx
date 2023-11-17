import SemesterScreen from "views/courseTimeTabling/SemesterScreen";
import ClassListOpenedScreen from "views/courseTimeTabling/ClassListOpenedScreen";
import ScheduleScreen from "views/courseTimeTabling/ScheduleScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function CourseTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={SemesterScreen}
          exact
          path={`${path}/semester`}
        ></Route>
        <Route
          component={ClassListOpenedScreen}
          exact
          path={`${path}/class-list-opened`}
        ></Route>
        <Route
          component={ScheduleScreen}
          exact
          path={`${path}/schedule`}
        ></Route>
      </Switch>
    </div>
  );
}
