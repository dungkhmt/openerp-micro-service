import { Route, Switch, useRouteMatch } from "react-router";
import AllRegisterClassScreen from "views/AllRegisterClassScreen";
import ApplicationResultScreen from "views/ApplicationResultScreen";
import RegisterClassForStudentScreen from "views/RegisterClassForStudentScreen";

export default function StudentRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={RegisterClassForStudentScreen}
          exact
          path={`${path}/class-register`}
        ></Route>
        <Route
          component={AllRegisterClassScreen}
          exact
          path={`${path}/classregister-list`}
        ></Route>
        <Route
          component={ApplicationResultScreen}
          exact
          path={`${path}/result`}
        ></Route>
      </Switch>
    </div>
  );
}
