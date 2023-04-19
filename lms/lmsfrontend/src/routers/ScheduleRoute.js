import React from "react";
import {Route, Switch, useRouteMatch} from "react-router";
import Upload from "../component/schedule/Upload";
import View from "../component/schedule/View";

export default function ScheduleRoute() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Upload} path={`${path}/upload`} />
        <Route component={View} path={`${path}/view`} />
      </Switch>
    </div>
  );
}
