import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import ScheduleInformationRouter from "./ScheduleInformationRouter";
// import SchedulePerformanceRouter from "./SchedulePerformanceRouter";
// import ScheduleScreen from "views/ScheduleScreen";
import CourseTimeTablingRouter from "./CourseTimeTablingRouter";
import GeneralTimeTablingRouter from "./GeneralTimeTablingRouter";
import FirstYearTimeTablingRouter from "./FirstYearTimeTablingRouter";
import ComputerLabTimeTabling from "./ComputerLabTimeTablingRouter";
import TaRecruitmentRouter from "./TaRecruitmentRouter";
import ThesisDefensePlanAssignmentRouter from "./ThesisDefensePlanAssignmentRouter";
import MainPage from "views/mainPage";
import AssetManagementRouter from "./AssetManagementRouter";
import TrainingFrogCourseRouter from "./TrainingFrogCourseRouter";
import ExamTimeTablingRouter from "./ExamTimeTablingRouter"
import WMSRouter from "./WMSRouter";
const styles = {
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: "0.5px",
    },
  },
};

function MainAppRouter(props) {
  const location = useLocation();
  const notificationState = useNotificationState();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          <Route component={MainPage} exact path="/" />
          <PrivateRoute component={TeacherRouter} path="/teacher" />
          {/* <PrivateRoute component={ScheduleScreen} path="/schedule" /> */}
          <PrivateRoute
            component={ScheduleInformationRouter}
            path="/schedule-information"
          />
          {/* <PrivateRoute component={SchedulePerformanceRouter} path="/schedule-performance" /> */}
          <PrivateRoute
            component={CourseTimeTablingRouter}
            path="/course-time-tabling"
          />
          {/* <Route component={error} path="*" /> */}
          <PrivateRoute
            component={GeneralTimeTablingRouter}
            path="/general-time-tabling"
          />
          <PrivateRoute
            component={WMSRouter}
            path="/wms"
          />
          <PrivateRoute
            component={FirstYearTimeTablingRouter}
            path="/first-year-time-tabling"
          />

          <PrivateRoute
            component={ComputerLabTimeTabling}
            path="/lab-time-tabling"
          />
          <PrivateRoute
            component={TaRecruitmentRouter}
            path="/ta-recruitment"
          />
          <PrivateRoute
            component={ThesisDefensePlanAssignmentRouter}
            path="/thesis"
          />
          <PrivateRoute
            component={AssetManagementRouter}
            path="/asset-management"
          />
           <PrivateRoute
            component={TrainingFrogCourseRouter}
            path="/training_course"
          />
          <PrivateRoute
            component={ExamTimeTablingRouter}
            path="/exam-time-tabling"
          />
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
