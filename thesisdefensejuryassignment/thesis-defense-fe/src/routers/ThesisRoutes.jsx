import { Route, Routes, useLocation } from "react-router";
import DefenseJury from "../component/education/thesisdefensejury/DefenseJury";
import DefenseJuryDetail from "../component/education/thesisdefensejury/DefenseJuryDetail";
import CreateDefenseJury from "../component/education/thesisdefensejury/CreateDefenseJury";
import Thesis from "../component/education/thesisdefensejury/Thesis";
import CreateThesis from "../component/education/thesisdefensejury/CreateThesis";
import EditThesis from "../component/education/thesisdefensejury/EditThesis";
import DefensePlanManager from "../component/education/thesisdefensejury/DefensePlanManager";
import ThesisDetail from "../component/education/thesisdefensejury/ThesisDetail";
import ThesisDefensePlans from "../component/education/thesisdefensejury/ThesisDefensePlans";
import AssginTeacherToPlan from "../component/education/thesisdefensejury/AssignTeacherToPlan";

export default function ThesisRoutes() {
  let { pathname } = useLocation();
  const path = pathname;
  return (
    <>
      <Route element={<CreateThesis />} path={`${path}/create`} exact />
      <Route element={<EditThesis />} path={`${path}/edit/:id`} exact />
      <Route
        element={<CreateDefenseJury />}
        path={`${path}/defense_jury/create`}
        exact
      />
      <Route
        element={<DefenseJuryDetail />}
        path={`${path}/defense_jury/:id`}
        exact
      />
      <Route
        element={<ThesisDefensePlans />}
        path={`${path}/thesis_defense_plan`}
        exact
      />
      <Route
        element={<DefensePlanManager />}
        path={`${path}/thesis_defense_plan/:id`}
        exact
      />
      <Route
        element={<AssginTeacherToPlan />}
        path={`${path}/defensePlan/:id/assignTeacher`}
        exact
      />
      <Route element={<DefenseJury />} path={`${path}/defense_jury`} />
      <Route element={<ThesisDetail />} path={`${path}/:id`} exact />
      <Route element={<Thesis />} path={`${path}`} />
    </>
  );
}
