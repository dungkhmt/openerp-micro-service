import { Route, Switch, useRouteMatch } from "react-router";
import CourseInfoPage from "views/TrainingProgCourse/test/CourseDetailScreen";
import AllCourseScreen from "views/TrainingProgCourse/AllCourseScreen";
import AddCourseScreen from "views/TrainingProgCourse/AllCourseScreen/AddCourseScreen";
export default function StudentRouter() {
  let {path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={CourseInfoPage}
          path={`${path}/teacher/course_list`}
          exact
        ></Route>  
        <Route
          component={AllCourseScreen}
          path={`${path}/teacher/course`}
          exact
        ></Route>

        <Route
          component={AddCourseScreen}
          path={`${path}/teacher/course/create`}
          exact
        ></Route>
      </Switch>
    </div>
  );
}
