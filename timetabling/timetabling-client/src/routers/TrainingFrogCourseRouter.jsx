import { Route, Switch, useRouteMatch } from "react-router";
import {AllCourseScreen} from "views/TrainingProgCourse/AllCourseScreen/index";
export default function StudentRouter() {
  let {path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={AllCourseScreen}
          path={`${path}/training_course/teacher/course_list`}
          exact
        ></Route>  
        <Route
          component={AllCourseScreen}
          path={`${path}/teacher/course`}
          exact
        ></Route>
      </Switch>
    </div>
  );
}
