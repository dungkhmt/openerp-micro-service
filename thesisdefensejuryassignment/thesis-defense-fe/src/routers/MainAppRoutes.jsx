import { LinearProgress } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import React, { lazy, Suspense, useEffect } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import PrivateRoute from "./PrivateRoute";
import { Home } from "../component";
import { Layout } from "../layout";
import { drawerWidth } from "../layout/sidebar/v1/SideBar";
import { useNotificationState } from "../state/NotificationState";
import DefenseJuryDetail from "../component/education/thesisdefensejury/DefenseJuryDetail";
import DefensePlanManager from "../component/education/thesisdefensejury/DefensePlanManager";
import ThesisDefensePlans from "../component/education/thesisdefensejury/ThesisDefensePlans";
import NotFound from "views/errors/NotFound";
import AssignTeacherAndThesisToDefenseJury from "../component/education/thesisdefensejury/AssignTeacherAndThesisToDefenseJury"; // import WhiteBoardRoute from "./WhiteBoardRoute";
import AssignedThesisDefensePlan from "views/thesis-defense-jury-assignment/assigned/AssignedThesisDefensePlan";
import AssignedDefenseJury from "views/thesis-defense-jury-assignment/assigned/AssignedDefenseJury";
import CreateThesis from "component/education/thesisdefensejury/CreateThesis";
import StudentAssignedDefenseJury from "views/thesis-defense-jury-assignment/student/StudentAssignedDefenseJury";
import PresidentAssignedThesisDefensePlan from "views/thesis-defense-jury-assignment/assigned/PresidentAssignedThesisDefensePlan";
import PresidentAssignedDefenseJury from "views/thesis-defense-jury-assignment/assigned/PresidentAssignedDefenseJury";
import PresidentAssignJuryDefense from "views/thesis-defense-jury-assignment/assigned/PresidentAssignJuryDefense";
import AssignTeacherAndThesisAutomatically from "views/thesis-defense-jury-assignment/manager/AssignTeacherAndThesisAutomatically";
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
                <Route
                  path="/thesis/thesis_defense_plan/:id/defense_jury/:juryId/create"
                  element={<AssignTeacherAndThesisToDefenseJury />}
                  exact
                />
                <Route
                  path="assign-automatically"
                  element={<AssignTeacherAndThesisAutomatically />}
                  exact
                />
              </Route>
              <Route path="assigned">
                <Route index element={<AssignedThesisDefensePlan />} />
                <Route path=":id" element={<AssignedDefenseJury />} exact />
                <Route
                  path="/thesis/thesis_defense_plan/assigned/:id/defense_jury/:juryId"
                  element={<DefenseJuryDetail />}
                  exact
                />
                <Route path="president" element={<PresidentAssignedThesisDefensePlan />} />
                <Route path="president/:id" element={<PresidentAssignedDefenseJury />} />
                <Route
                  path="/thesis/thesis_defense_plan/assigned/president/:id/defense_jury/:juryId"
                  element={<DefenseJuryDetail />}
                  exact
                />
                <Route path="/thesis/thesis_defense_plan/assigned/president/:id/defense_jury/:juryId/assign" element={<PresidentAssignJuryDefense />} />
              </Route>
            </Route>
            <Route path="/thesis/student">
              <Route path="thesis_defense_plan/assigned" element={<StudentAssignedDefenseJury />} />
              <Route path="create" element={<CreateThesis />}>
              </Route>
              <Route
                path="/thesis/student/thesis_defense_plan/assigned/:id/defense_jury/:juryId"
                element={<DefenseJuryDetail />}
                exact
              />
            </Route>
          </Route>
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default MainAppRoute;
