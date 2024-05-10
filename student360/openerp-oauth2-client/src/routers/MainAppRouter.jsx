import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import StudentListScreen from "views/Student/StudentListScreen/StudentListScreen";
import StudentDetailStatistics from "views/Student/StudentDetailStatistics/StudentDetailStatistics";
import Dashboard from "views/Dashboard";

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
  const { keycloak } = useKeycloak();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          <Route component={Dashboard} exact path="/" />
          <TeacherRouter component={StudentListScreen} exact path="/students" />
          <PrivateRoute
            component={StudentDetailStatistics}
            exact
            path="/students/statistics-detail/:id"
          />
          <PrivateRoute
            component={StudentDetailStatistics}
            exact
            path={"/student_result"}
          />

          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
