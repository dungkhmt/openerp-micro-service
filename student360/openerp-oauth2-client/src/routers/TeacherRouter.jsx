import StudentListScreen from "views/Student/StudentListScreen/StudentListScreen";
import { Route, Switch, useRouteMatch } from "react-router";

export default function TeacherRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={StudentListScreen} exact path={`${path}`}></Route>
      </Switch>
    </div>
  );
}
