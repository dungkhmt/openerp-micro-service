import { Route, Switch, useRouteMatch } from "react-router";
import AllClassScreen from "views/AllClassScreen";
import AssigningScreen from "views/AssigningScreen";
import ClassInformationScreen from "views/ClassInformationScreen";
import RegisterClassScreen from "views/RegisterClassScreen";
import RequestApprovalScreen from "views/RequestApprovalScreen";

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
        <Route
          component={RequestApprovalScreen}
          exact
          path={`${path}/request-approval`}
        ></Route>
        <Route
          component={AssigningScreen}
          exact
          path={`${path}/ta-assignment`}
        ></Route>
      </Switch>
    </div>
  );
}
