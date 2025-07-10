import { Route, Switch, useRouteMatch } from "react-router";
import ClassManagementScreen from "views/computerLabTimeTabling/ClassManagementScreen";
import RoomManagementScreen from "views/computerLabTimeTabling/RoomManagementScreen";
import RoomAvailibilityScreen from "views/computerLabTimeTabling/RoomAvailibilityScreen";
import TimetableScreen from "views/computerLabTimeTabling/TimetableScreen";
import ConflictCheckingScreen from "views/computerLabTimeTabling/ConflictCheckingScreen";
import AutoAssignScreen from "views/computerLabTimeTabling/AutoAssignScreen";
import ManualAssignScreen from "views/computerLabTimeTabling/ManualAssignScreen";

export default function ComputerLabTimeTabling() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ClassManagementScreen}
          exact
          path={`${path}/class-management`}
        ></Route>
        <Route
          component={RoomManagementScreen}
          exact
          path={`${path}/room-management`}
        ></Route>
        <Route
          component={RoomAvailibilityScreen}
          exact
          path={`${path}/room-availibility`}
        ></Route>
        <Route
          component={TimetableScreen}
          exact
          path={`${path}/timetable`}
        ></Route>
        <Route
          component={ConflictCheckingScreen}
          exact
          path={`${path}/conflict-checking`}
        ></Route>
        <Route
          component={ManualAssignScreen}
          exact
          path={`${path}/manual-assign`}
        ></Route>
        <Route
          component={AutoAssignScreen}
          exact
          path={`${path}/auto-assign`}
        ></Route>
      </Switch>
    </div>
  );
}
