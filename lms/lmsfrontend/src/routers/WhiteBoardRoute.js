import React from 'react'
import {Route, Switch, useRouteMatch} from 'react-router'
import CreateWhiteBoard from '../component/education/whiteboard/CreateWhiteBoard'
import ListWhiteBoard from '../component/education/whiteboard/ListWhiteBoard'
import {MainBoard} from '../component/education/whiteboard/MainBoard'
import SocketContextProvider, {socket} from '../utils/whiteboard/context/SocketContext'

export default function WhiteBoardRoute() {
  let { path } = useRouteMatch()
  return (
    <SocketContextProvider value={socket}>
      <Switch>
        <Route component={CreateWhiteBoard} exact path={`${path}/board`}></Route>
        <Route component={ListWhiteBoard} exact path={`${path}/board/list`}></Route>
        <Route component={MainBoard} exact path={`${path}/board/:whiteboardId`} />
      </Switch>
    </SocketContextProvider>
  )
}
