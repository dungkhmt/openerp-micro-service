import { Route, Switch, useRouteMatch } from "react-router";
import EmptyRoomFindingScreen from "views/general-time-tabling/empty-room-find/EmptyRoomFindingScreen";
import GeneralGroupScreen from "views/general-time-tabling/general-group-select/GeneralGroupScreen";
import GeneralPlanClassOpenScreen from "views/general-time-tabling/general-plan-class-open/GeneralPlanClassOpenScreen";
import GeneralScheduleScreen from "views/general-time-tabling/general-schedule/GeneralScheduleScreen";
import GeneralUploadScreen from "views/general-time-tabling/general-upload/GeneralUploadScreen";
import RoomOccupationScreen from "views/general-time-tabling/room-occupation/RoomOccupationScreen";

export default function GeneralTimeTablingRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          //component={GeneralPlanClassOpenScreenV2}
          component={GeneralPlanClassOpenScreen}
          exact
          path={`${path}/plan-class-open`}
        ></Route>
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
        <Route
          component={EmptyRoomFindingScreen}
          exact
          path={`${path}/find-empty-room`}
        ></Route>
      </Switch>
    </div>
  );
}
