import ClassCodeScreen from "views/scheduleInformation/ClassCodeScreen";
import ClassRoomScreen from "views/scheduleInformation/ClassRoomScreen";
import ClassTypeScreen from "views/scheduleInformation/ClassTypeScreen";
import InstituteScreen from "views/scheduleInformation/InstituteScreen";
import ManagementCodeScreen from "views/scheduleInformation/ManagementCodeScreen";
import ModuleScreen from "views/scheduleInformation/ModuleScreen";
import OpenBatchScreen from "views/scheduleInformation/OpenBatchScreen";
import SemesterScreen from "views/scheduleInformation/SemesterScreen";
import StateScreen from "views/scheduleInformation/StateScreen";
import StudyTimeScreen from "views/scheduleInformation/StudyTimeScreen";
import StudyWeekScreen from "views/scheduleInformation/StudyWeekScreen";
import WeekDayScreen from "views/scheduleInformation/WeekDayScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function TeacherRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ClassCodeScreen}
          exact
          path={`${path}/class-code`}
        ></Route>
        <Route
          component={ClassRoomScreen}
          exact
          path={`${path}/classroom`}
        ></Route>
        <Route
          component={ClassTypeScreen}
          exact
          path={`${path}/class-type`}
        ></Route>
        <Route
          component={InstituteScreen}
          exact
          path={`${path}/institute`}
        ></Route>
        <Route
          component={ManagementCodeScreen}
          exact
          path={`${path}/management-code`}
        ></Route>
        <Route
          component={ModuleScreen}
          exact
          path={`${path}/module`}
        ></Route>
        <Route
          component={OpenBatchScreen}
          exact
          path={`${path}/open-batch`}
        ></Route>
        <Route
          component={SemesterScreen}
          exact
          path={`${path}/semester`}
        ></Route>
        <Route
          component={StateScreen}
          exact
          path={`${path}/state`}
        ></Route>
        <Route
          component={StudyTimeScreen}
          exact
          path={`${path}/study-time`}
        ></Route>
        <Route
          component={StudyWeekScreen}
          exact
          path={`${path}/study-week`}
        ></Route>
        <Route
          component={WeekDayScreen}
          exact
          path={`${path}/week-day`}
        ></Route>
      </Switch>
    </div>
  );
}
