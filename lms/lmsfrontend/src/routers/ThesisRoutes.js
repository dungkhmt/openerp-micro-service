import {Route, Switch, useRouteMatch} from "react-router";
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
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
     
         <Route
          component={CreateThesis}
          path={`${path}/create`}
          exact
        />

         <Route
          component={EditThesis}
          path={`${path}/edit/:id`}
          exact
        />
      <Route
          component={CreateDefenseJury}
          path={`${path}/defense_jury/create`}
          exact
        />
      <Route
          component={DefenseJuryDetail}
          path={`${path}/defense_jury/:id`}
          exact
        />
        
       <Route
          component={ThesisDefensePlans}
          path={`${path}/thesis_defense_plan`}
          exact
        />

      <Route
          component={DefensePlanManager}
          path={`${path}/thesis_defense_plan/:id`}
          exact
        />

        <Route
          component={AssginTeacherToPlan}
          path={`${path}/defensePlan/:id/assignTeacher`}
          exact
        />
     
      
        
        <Route
          component={DefenseJury}
          path={`${path}/defense_jury`}
        />

        <Route
          component={ThesisDetail}
          path={`${path}/:id`}
          exact
        />

        <Route
          component={Thesis}
          path={`${path}`}
        />

        
        
        
        
        
      </Switch>
    </div>
  );
}
