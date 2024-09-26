import MainDashBoard from "component/dataadmin/MainDashBoard";
import ViewCourseVideo from "component/dataadmin/ViewCourseVideo";
import ViewLogUserDoPraticeQuizs from "component/dataadmin/ViewLogUserDoPraticeQuizs";
import ViewProgrammingContestSubmission from "component/dataadmin/ViewProgrammingContestSubmission";
import { Routes, Route, useLocation } from "react-router";
import LearningProfileList from "views/dataadmin/viewlearningprofiles/LearningProfileList";
import StudentLearningProfiles from "views/dataadmin/viewlearningprofiles/StudentLearningProfiles";

export default function AdminRoute() {
  let { pathName } = useLocation();
  return (
    <div>
      <Routes>
        <Route
          component={MainDashBoard}
          exact
          path={`${pathName}/dashboard/main`}
        ></Route>
        <Route
          component={ViewCourseVideo}
          exact
          path={`${pathName}/view-course-video/list`}
        ></Route>
        <Route
          component={ViewProgrammingContestSubmission}
          exact
          path={`${pathName}/view-programming-contest-submission/list`}
        ></Route>

        <Route
          component={ViewLogUserDoPraticeQuizs}
          exact
          path={`${pathName}/view-log-user-do-pratice-quiz/list`}
        ></Route>

        <Route
          path={`${pathName}/view-learning-profiles/users`}
          component={LearningProfileList}
          exact
        />
        <Route
          path={`${pathName}/view-learning-profiles/users/:studentLoginId`}
          component={StudentLearningProfiles}
          exact
        />
      </Routes>
    </div>
  );
}
