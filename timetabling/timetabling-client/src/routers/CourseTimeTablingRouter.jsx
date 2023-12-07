import SemesterScreen from "views/courseTimeTabling/semester/SemesterScreen";
import ClassListOpenedScreen from "views/courseTimeTabling/classListOpened/ClassListOpenedScreen";
import ScheduleScreen from "views/courseTimeTabling/schedule/ScheduleScreen";
import MakeScheduleScreen from "views/courseTimeTabling/makeSchedule/MakeScheduleScreen";
import ClassroomListScreen from "views/courseTimeTabling/classroom/ClassroomListScreen";
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
        <Route
          component={MakeScheduleScreen}
          exact
          path={`${path}/make-schedule`}
        ></Route>
        <Route
          component={ClassroomListScreen}
          exact
          path={`${path}/classroom`}
        ></Route>
      </Switch>
    </div>
  );
}
