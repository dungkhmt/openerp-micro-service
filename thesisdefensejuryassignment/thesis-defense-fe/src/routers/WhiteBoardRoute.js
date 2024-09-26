import React from "react";
import { Route, Routes, useLocation } from "react-router";
import CreateWhiteBoard from "../component/education/whiteboard/CreateWhiteBoard";
import ListWhiteBoard from "../component/education/whiteboard/ListWhiteBoard";
import { MainBoard } from "../component/education/whiteboard/MainBoard";
import SocketContextProvider, {
  socket,
} from "../utils/whiteboard/context/SocketContext";

export default function WhiteBoardRoute() {
  let { pathname } = useLocation();
  const path = pathname;
  return (
    <SocketContextProvider value={socket}>
      <Routes>
        <Route
          component={CreateWhiteBoard}
          exact
          path={`${path}/board`}
        ></Route>
        <Route
          component={ListWhiteBoard}
          exact
          path={`${path}/board/list`}
        ></Route>
        <Route
          component={MainBoard}
          exact
          path={`${path}/board/:whiteboardId`}
        />
      </Routes>
    </SocketContextProvider>
  );
}
