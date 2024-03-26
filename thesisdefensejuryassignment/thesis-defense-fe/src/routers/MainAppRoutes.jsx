import { LinearProgress } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React, { lazy, Suspense, useEffect } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import PrivateRoute from "./PrivateRoute";
import { Home } from "../component";
import { Layout } from "../layout";
import { drawerWidth } from "../layout/sidebar/v1/SideBar";
import { useNotificationState } from "../state/NotificationState";
import DefenseJury from "../component/education/thesisdefensejury/DefenseJury";
import DefenseJuryDetail from "../component/education/thesisdefensejury/DefenseJuryDetail";
import Thesis from "../component/education/thesisdefensejury/Thesis";
import CreateThesis from "../component/education/thesisdefensejury/CreateThesis";
import EditThesis from "../component/education/thesisdefensejury/EditThesis";
import DefensePlanManager from "../component/education/thesisdefensejury/DefensePlanManager";
import ThesisDetail from "../component/education/thesisdefensejury/ThesisDetail";
import ThesisDefensePlans from "../component/education/thesisdefensejury/ThesisDefensePlans";
import AssginTeacherToPlan from "../component/education/thesisdefensejury/AssignTeacherAndThesisToDefenseJury";
import NotFound from "views/errors/NotFound";
// import NotFound from "../views/errors/NotFound";
// import AdminRoute from "./AdminRoute";
// import ProgrammingContestRoutes from "./ProgrammingContestRoutes";
import AssignTeacherAndThesisToDefenseJury from "../component/education/thesisdefensejury/AssignTeacherAndThesisToDefenseJury"; // import WhiteBoardRoute from "./WhiteBoardRoute";
// import UploadUser from "../component/userlogin/UploadUser";
// import ContestManagerRankingPublicV2 from "../component/education/programmingcontestFE/ContestManagerRankingPublicV2";

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
      <Suspense fallback={<LinearProgress sx={classes.loadingProgress} />}>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route element={<NotFound />} path="*" />
          <Route element={<PrivateRoute />}>
            <Route path={`/thesis/thesis_defense_plan`}>
              <Route index element={<ThesisDefensePlans />} />
              <Route path={`:id`}>
                <Route index element={<DefensePlanManager />} />
                <Route
                  path="/thesis/thesis_defense_plan/:id/defense_jury/:juryId"
                  element={<DefenseJuryDetail />}
                  exact
                />
              </Route>
            </Route>
            {/* <Route element={<CreateThesis />} path={`/thesis/create`} exact />
            <Route element={<EditThesis />} path={`/thesis/edit/:id`} exact /> */}

            {/* <Route
              element={<DefenseJuryDetail />}
              path={`/thesis/defense_jury/:id`}
              exact
            /> */}
            {/* 
            <Route
              element={<AssginTeacherToPlan />}
              path={`/thesis/defensePlan/:id/assignTeacher`}
              exact
            />
            <Route element={<DefenseJury />} path={`/thesis/defense_jury`} />
            <Route element={<ThesisDetail />} path={`/thesis/:id`} exact />
            <Route element={<Thesis />} path={`/thesis`} /> */}
          </Route>
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default MainAppRoute;
