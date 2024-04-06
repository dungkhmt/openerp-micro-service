import SemesterScreen from "views/courseTimeTabling/semester/SemesterScreen";
import ClassListOpenedScreen from "views/courseTimeTabling/classListOpened/ClassListOpenedScreen";
import FirstYearStandardClass from "views/firstyearstandard/FirstYearStandardClass";
import ScheduleScreen from "views/courseTimeTabling/schedule/ScheduleScreen";
import MakeScheduleScreen from "views/courseTimeTabling/makeSchedule/MakeScheduleScreen";
import ClassroomListScreen from "views/courseTimeTabling/classroom/ClassroomListScreen";
import { Route, Switch, useRouteMatch } from "react-router";
import GeneralScheduleScreen from "views/courseTimeTabling/general-schedule/GeneralScheduleScreen";
import RoomOccupationScreen from "views/courseTimeTabling/room-occupation/RoomOccupationScreen";

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
          component={GeneralScheduleScreen}
          exact
          path={`${path}/auto-fill-general-schedule`}
        ></Route>
        <Route
          component={RoomOccupationScreen}
          exact
          path={`${path}/room-occupation`}
        ></Route>
        <Route
          component={ClassroomListScreen}
          exact
          path={`${path}/classroom`}
        ></Route>

        <Route
          component={FirstYearStandardClass}
          exact
          path={`${path}/class-list-opened-first-year-standard`}
        ></Route>
      </Switch>
    </div>
  );
}
