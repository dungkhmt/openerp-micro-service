import { Route, Switch, useRouteMatch } from "react-router";
import CourseInfoPage from "views/TrainingProgCourse/test/CourseDetailScreen";
import AllCourseScreen from "views/TrainingProgCourse/AllCourseScreen";
import AddCourseScreen from "views/TrainingProgCourse/AllCourseScreen/AddCourseScreen";
import CourseDetailPage from "views/TrainingProgCourse/AllCourseScreen/CourseDetail";
import UpdateCoursePage from "views/TrainingProgCourse/AllCourseScreen/UpdateCourse";
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

        <Route
          component={CourseDetailPage}
          path={`${path}/teacher/course/:courseId`}
          exact
        ></Route>
        
        <Route
          component={UpdateCoursePage}
          path={`${path}/teacher/course/:courseId/edit`}
          exact
        ></Route>

      </Switch>
    </div>
  );
}
