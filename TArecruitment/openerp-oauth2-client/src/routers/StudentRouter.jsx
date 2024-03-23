import { Route, Switch, useRouteMatch } from "react-router";
import AllRegisterClassScreen from "views/AllRegisterClassScreen";
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
        {/**
         * @todo: Add route for what class the student has applied
         */}
      </Switch>
    </div>
  );
}
