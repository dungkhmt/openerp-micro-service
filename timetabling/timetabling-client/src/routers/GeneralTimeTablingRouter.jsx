import { Route, Switch, useRouteMatch } from "react-router";
import GeneralGroupScreen from "views/general-time-tabling/general-group-select/GeneralGroupScreen";
import GeneralScheduleScreen from "views/general-time-tabling/general-schedule/GeneralScheduleScreen";
import GeneralUploadScreen from "views/general-time-tabling/general-upload/GeneralUploadScreen";
import RoomOccupationScreen from "views/general-time-tabling/room-occupation/RoomOccupationScreen";

export default function GeneralTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={GeneralUploadScreen}
          exact
          path={`${path}/upload-class`}
        ></Route>
        <Route
          component={GeneralGroupScreen}
          exact
          path={`${path}/group-class`}
        ></Route>
        <Route
          component={GeneralScheduleScreen}
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
