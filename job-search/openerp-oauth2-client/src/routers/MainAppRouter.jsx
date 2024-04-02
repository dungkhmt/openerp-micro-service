import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import DemoScreen from "views/DemoScreen";
import CreateNewCvForm from "views/cv/CreateCV";
import UserSkill from "views/skill/Skill";
import CreateJobPost from "views/jobpost/CreateJobPost";
import ViewAllJobPost from "views/jobpost/ViewAllJobPost";
import ViewJobPostDetail from "views/jobpost/ViewJobPostDetail";

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
          <Route component={() => <></>} exact path="/" />
          <PrivateRoute component={DemoScreen} exact path="/demo" />
          <PrivateRoute component={TeacherRouter} path="/teacher" />
          <PrivateRoute component={CreateNewCvForm} path="/create-cv" />
          <PrivateRoute component={UserSkill} path = "/user-skill" />
          <PrivateRoute component={CreateJobPost} path = "/create-job-post" />
          <PrivateRoute component={ViewJobPostDetail} path = "/view-job-post/:id" />          
          <PrivateRoute component={ViewAllJobPost} path = "/view-job-post" />
          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
