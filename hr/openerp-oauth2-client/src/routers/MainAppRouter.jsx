import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import EmployeeRouter from "./EmployeeRouter";
import CheckpointEvaluationRouter from "./CheckpointEvaluationRouter";
import DemoScreen from "views/DemoScreen";
import EmployeeDetailsRouter from "./EmployeeDetailsRouter";
import DepartmentScreen from "views/DepartmentScreen";
import JobPositionScreen from "views/JobPositionScreen";
import CheckpointConfigureScreen from "views/CheckpointConfigureScreen";
import CheckpointPeriodScreen from "views/CheckpointPeriodScreen";
import CheckpointEvaluationScreen from "views/CheckpointEvaluationScreen";
import StaffScreen from "views/StaffScreen";

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
          <Route component={CheckpointPeriodScreen} exact path="/" />
          
          <PrivateRoute component={DepartmentScreen} exact path="/department" />
          <PrivateRoute component={JobPositionScreen} exact path="/job-position" />
          <PrivateRoute component={CheckpointConfigureScreen} exact path="/checkpoint/configure" />
          <PrivateRoute component={CheckpointPeriodScreen} exact path="/checkpoint/period" />
          <PrivateRoute component={StaffScreen} exact path="/staff" />
          <Route component={EmployeeDetailsRouter} path="/employee" />
          <PrivateRoute component={CheckpointEvaluationRouter} path="/checkpoint-evaluation" />
          
          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
