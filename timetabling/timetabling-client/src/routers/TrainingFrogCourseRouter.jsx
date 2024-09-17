import { Route, Switch, useRouteMatch } from "react-router";
import CourseInfoPage from "views/TrainingProgCourse/test/CourseDetailScreen";
import AllCourseScreen from "views/TrainingProgCourse/AllCourseScreen";
import AddCourseScreen from "views/TrainingProgCourse/AllCourseScreen/AddCourseScreen";
import CourseDetailPage from "views/TrainingProgCourse/AllCourseScreen/CourseDetail";
import UpdateCoursePage from "views/TrainingProgCourse/AllCourseScreen/UpdateCourse";
import AllProgramScreen from "views/TrainingProgCourse/AllProgramScreen";
import AddProgramScreen from "views/TrainingProgCourse/AllProgramScreen/AddProgramScreen";
import ProgramDetailPage from "views/TrainingProgCourse/AllProgramScreen/ProgramScreenDetail";
import UpdateProgramPage from "views/TrainingProgCourse/AllProgramScreen/UpdateProgramScreenDetail";
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

        <Route
          component={AllProgramScreen}
          path={`${path}/teacher/program`}
          exact
        ></Route>

        <Route
          component={AddProgramScreen}
          path={`${path}/teacher/program/create`}
          exact
        ></Route>

        <Route
          component={ProgramDetailPage}
          path={`${path}/teacher/program/:programId`}
          exact
        ></Route>
         <Route
          component={UpdateProgramPage}
          path={`${path}/teacher/program/edit/:programId`}
          exact
        ></Route>
      </Switch>
    </div>
  );
}
