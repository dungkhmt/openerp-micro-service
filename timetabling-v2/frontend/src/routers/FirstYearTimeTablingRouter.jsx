import { Route, Switch, useRouteMatch } from "react-router";
import FirstYearGroupScreen from "views/first-year-time-tabling/first-year-group-select/FirstYearGroupScreen";
import FirstYearPlanClassOpenScreen from "views/first-year-time-tabling/first-year-plan-class-open/FirstYearPlanClassOpenScreen";
import FirstYearScheduleScreen from "views/first-year-time-tabling/first-year-schedule/FirstYearScheduleScreen";
import FirstYearUploadScreen from "views/first-year-time-tabling/first-year-upload/FirstYearUploadScreen";
import RoomOccupationScreen from "views/first-year-time-tabling/room-occupation/RoomOccupationScreen";

export default function FirstYearTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={FirstYearPlanClassOpenScreen}
          exact
          path={`${path}/plan-class-open`}
        ></Route>
        <Route
          component={FirstYearUploadScreen}
          exact
          path={`${path}/upload-class`}
        ></Route>
        <Route
          component={FirstYearGroupScreen}
          exact
          path={`${path}/group-class`}
        ></Route>
        <Route
          component={FirstYearScheduleScreen}
          exact
          path={`${path}/schedule-class`}
        ></Route>
        <Route
          component={RoomOccupationScreen}
          exact
          path={`${path}/room-occupation`}
        ></Route>
      </Switch>
    </div>
  );
}
