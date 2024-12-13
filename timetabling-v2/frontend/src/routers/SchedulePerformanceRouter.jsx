import TimePerformanceScreen from "views/schedulePerformance/TimePerformanceScreen";
import SeatPerformanceScreen from "views/schedulePerformance/SeatPerformanceScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function TeacherRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={TimePerformanceScreen}
          exact
          path={`${path}/time-performance`}
        ></Route>
        <Route
          component={SeatPerformanceScreen}
          exact
          path={`${path}/seat-performance`}
        ></Route>
      </Switch>
    </div>
  );
}
