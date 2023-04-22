import {LinearProgress} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React, {lazy, Suspense, useEffect} from "react";
import {Route, Switch, useLocation} from "react-router-dom";
import PrivateRoute from "../common/PrivateRoute";
import {Home} from "../component";
import {Layout} from "../layout";
import {drawerWidth} from "../layout/sidebar/v1/SideBar";
import {useNotificationState} from "../state/NotificationState";
import NotFound from "../views/errors/NotFound";
import AccountActivationRoute from "./AccountActivationRoute";
import AdminRoute from "./AdminRoute";
import ProgrammingContestRoutes from "./ProgrammingContestRoutes";
import ThesisRoutes from "./ThesisRoutes";
import TaskManagementRoute from "./TaskManagementRoute";
import WhiteBoardRoute from "./WhiteBoardRoute";
import ContestManagerRankingPublic from "../component/education/programmingcontestFE/ContestManagerRankingPublic";

const EduRoute = lazy(() => import("./EduRoute"));
const UserLoginRoute = lazy(() => import("./UserLoginRoute"));

const useStyles = makeStyles(() => ({
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: 0.5,
    },
  },
}));

function MainAppRoute(props) {
  const location = useLocation();
  const notificationState = useNotificationState();

  //
  const classes = useStyles();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense
        fallback={<LinearProgress className={classes.loadingProgress} />}
      >
        <Switch>
          <Route component={Home} exact path="/" />
          <Route
            component={ContestManagerRankingPublic}
            path={"/programming-contest/public/contest-raking/:contestId"}
          />

          {/*<PrivateRoute component={UserLoginRoute} path="/userlogin" />*/}

          <PrivateRoute component={EduRoute} path="/edu" />
          <PrivateRoute
            component={TaskManagementRoute}
            path="/taskmanagement"
          />
          <PrivateRoute component={WhiteBoardRoute} path="/whiteboard" />

          <PrivateRoute component={AdminRoute} path="/admin/data" />
          {/*<PrivateRoute component={AccountActivationRoute} path="/activation" />*/}
          <PrivateRoute
            component={ProgrammingContestRoutes}
            path="/programming-contest"
          />
          <PrivateRoute component={ThesisRoutes} path="/thesis" />

          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRoute;
