import ClassRoomScreen from "views/courseTimeTabling/classroom/ClassroomListScreen";
import ManagementCodeScreen from "views/scheduleInformation/ManagementCodeScreen";
import ModuleScreen from "views/scheduleInformation/ModuleScreen";
import SemesterScreen from "views/courseTimeTabling/semester/SemesterScreen";
import WeekDayScreen from "views/scheduleInformation/WeekDayScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function TeacherRouter() {
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
          component={ClassRoomScreen}
          exact
          path={`${path}/classroom`}
        ></Route>
        <Route
          component={ManagementCodeScreen}
          exact
          path={`${path}/class-pertiod`}
        ></Route>
        <Route
          component={WeekDayScreen}
          exact
          path={`${path}/week-day`}
        ></Route>
        <Route
          component={ModuleScreen}
          exact
          path={`${path}/group`}
        ></Route>
      </Switch>
    </div>
  );
}
