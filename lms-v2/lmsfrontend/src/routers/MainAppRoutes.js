import {LinearProgress} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import React, {lazy, Suspense, useEffect} from "react";
import {Route, Switch, useLocation} from "react-router-dom";
import PrivateRoute from "./PrivateRoute";
import {Home} from "../component";
import {Layout} from "../layout";
import {drawerWidth} from "../layout/sidebar/v1/SideBar";
import {useNotificationState} from "../state/NotificationState";
import NotFound from "../views/errors/NotFound";
import AdminRoute from "./AdminRoute";
import ProgrammingContestRoutes from "./ProgrammingContestRoutes";
import ThesisRoutes from "./ThesisRoutes";
import WhiteBoardRoute from "./WhiteBoardRoute";
import UploadUser from "../component/userlogin/UploadUser";
import ExamClassList from "../component/examclassaccount/ExamClassList";
import ExamClassCreate from "../component/examclassaccount/ExamClassCreate";
import ExamClassDetail from "../component/examclassaccount/ExamClassDetail";
import UploadGeneratedUser from "../component/examclassaccount/UploadGeneratedUser";

import ContestManagerRankingPublicV2 from "../component/education/programmingcontestFE/ContestManagerRankingPublicV2";
import ExamRoute from "./ExamRoute";

const EduRoute = lazy(() => import("./EduRoute"));
// const UserLoginRoute = lazy(() => import("./UserLoginRoute"));

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
            component={ContestManagerRankingPublicV2}
            path={"/programming-contest/public/:contestId/ranking"}
          />

          {/* consider remove */}
          {/*<PrivateRoute component={UserLoginRoute} path="/userlogin" />*/}

          <PrivateRoute component={EduRoute} path="/edu" />
          <PrivateRoute component={WhiteBoardRoute} path="/whiteboard" />

          <PrivateRoute component={AdminRoute} path="/admin/data" />
          <PrivateRoute component={ExamRoute} path="/exam" />
          <PrivateRoute
            component={ProgrammingContestRoutes}
            path="/programming-contest"
          />
          <PrivateRoute component={ThesisRoutes} path="/thesis" />

          <PrivateRoute
            component={UploadUser}
            exact
            path={`/user/upload`}
          />
           <PrivateRoute
            component={ExamClassList}
            exact
            path={`/exam-class/list`}
          />
          <PrivateRoute
            component={ExamClassCreate}
            exact
            path={`/exam-class/create`}
          />
          <PrivateRoute
            component={ExamClassDetail}
            exact
            path={`/exam-class/detail/:id`}
          />
          <PrivateRoute
            component={UploadGeneratedUser}
            exact
            path={`/generated-user/upload`}
          />

          {/* <Route component={error} path="*" /> */}
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRoute;
