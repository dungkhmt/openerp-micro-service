import ProtectedScreen from "components/ProtectedScreen";
import { Route, Switch, useRouteMatch } from "react-router";
import CodeEditorPage from "views/code-editor/CodeEditorPage";
import CreateJoinRoomPage from "views/create-join-room/CreateJoinRoomPage";
import MyRoomsPage from "views/my-rooms/MyRoomsPage";

export default function CodeEditorOnlineRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={CreateJoinRoomPage} exact path={`${path}/create-join-room`} />
        <Route component={CodeEditorPage} exact path={`${path}/room/:id`} />
        <Route component={MyRoomsPage} exact path={`${path}/my-rooms`} />
      </Switch>
    </div>
  );
}
