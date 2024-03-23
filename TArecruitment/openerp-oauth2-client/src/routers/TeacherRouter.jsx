import { Route, Switch, useRouteMatch } from "react-router";
import AllClassScreen from "views/AllClassScreen";
import ClassInformationScreen from "views/ClassInformationScreen";
import RegisterClassScreen from "views/RegisterClassScreen";

export default function TeacherRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={RegisterClassScreen}
          exact
          path={`${path}/create-class`}
        ></Route>
        <Route
          component={AllClassScreen}
          exact
          path={`${path}/class-list`}
        ></Route>
        <Route
          component={ClassInformationScreen}
          exact
          path={`${path}/class-information/:id`}
        ></Route>
      </Switch>
    </div>
  );
}
