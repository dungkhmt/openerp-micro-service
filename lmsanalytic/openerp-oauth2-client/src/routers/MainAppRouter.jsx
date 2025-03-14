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
import LmsLogs from "components/lmslog/logs";
import FBGroups from "components/fb/groups";
import FBUsers from "components/fb/users";

import ContestListForRanking from "components/lmslog/contestrankinglist";
import ContestRanking from "components/lmslog/contestranking";
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
          <PrivateRoute component={LmsLogs} exact path="/lmslog/logs" />
          <PrivateRoute component={FBGroups} exact path="/fb/groups" />
          <PrivateRoute component={FBUsers} exact path="/fb/users" />
          

          <PrivateRoute component={ContestListForRanking} exact path="/contest/ranking" />
          <PrivateRoute component={ContestRanking} exact path={`/contest/ranking/:contestId`}/>
          
          <PrivateRoute component={TeacherRouter} path="/teacher" />

          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
