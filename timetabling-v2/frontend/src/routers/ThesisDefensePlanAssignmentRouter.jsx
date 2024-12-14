import { Route, Switch, useRouteMatch } from "react-router";
import ThesisDefensePlans from "views/thesisdefensejuryassignment/manager/ThesisDefensePlans";
import DefensePlanManager from "views/thesisdefensejuryassignment/manager/DefensePlanManager";
import DefenseJuryDetail from "components/thesisdefensejury/DefenseJuryDetail";
import AssignTeacherAndThesisToDefenseJury from "views/thesisdefensejuryassignment/manager/AssignTeacherAndThesisToDefenseJury";
// import AssignTeacherAndThesisAutomatically from "views/thesisdefensejuryassignment/manager/AssignTeacherAndThesisAutomatically";
import AssignedThesisDefensePlan from "views/thesisdefensejuryassignment/assigned/AssignedThesisDefensePlan";
import AssignedDefenseJury from "views/thesisdefensejuryassignment/assigned/AssignedDefenseJury";
import PresidentAssignedThesisDefensePlan from "views/thesisdefensejuryassignment/assigned/PresidentAssignedThesisDefensePlan";
import PresidentAssignedDefenseJury from "views/thesisdefensejuryassignment/assigned/PresidentAssignedDefenseJury";
import PresidentAssignJuryDefense from "views/thesisdefensejuryassignment/assigned/PresidentAssignJuryDefense";
import StudentAssignedDefenseJury from "views/thesisdefensejuryassignment/student/StudentAssignedDefenseJury";
import StudentCreateThesis from "views/thesisdefensejuryassignment/student/StudentCreateThesis";
import { EditThesisDefensePlan } from "views/thesisdefensejuryassignment/manager/EditThesisDefensePlan";
import { EditDefenseJury } from "views/thesisdefensejuryassignment/manager/EditDefenseJury";
import { EditTeacherAndThesisToDefenseJury } from "views/thesisdefensejuryassignment/manager/EditTeacherAndThesisToDefenseJury";
import { ManagerDefenseJuryDetail } from "views/thesisdefensejuryassignment/manager/ManagerDefenseJuryDetail";
import SuperviseThesisList from "views/thesisdefensejuryassignment/assigned/SuperviseThesisList";
import AssignSupervisedThesis from "views/thesisdefensejuryassignment/assigned/AssignSupervisedThesis";
import JuryTopicList from "views/thesisdefensejuryassignment/manager/JuryTopicList";
const ThesisDefensePlanAssignmentRouter = () => {
    let { path } = useRouteMatch();
    return (
        <>
            <Switch>
                <Route
                    component={ThesisDefensePlans}
                    exact
                    path={`${path}/thesis_defense_plan`}
                />
                <Route
                    component={DefensePlanManager}
                    path={`${path}/thesis_defense_plan/:id`}
                    exact
                />
                <Route
                    component={EditThesisDefensePlan}
                    path={`${path}/thesis_defense_plan/:id/edit`}
                    exact
                />
                <Route
                    path={`${path}/thesis_defense_plan/:id/defense_jury/:juryId`}
                    component={ManagerDefenseJuryDetail}
                    exact
                />
                <Route
                    path={`${path}/thesis_defense_plan/:id/defense_jury/:juryId/edit`}
                    component={EditDefenseJury}
                    exact
                />
                <Route
                    path={`${path}/thesis_defense_plan/:id/defense_jury/:juryId/create`}
                    component={AssignTeacherAndThesisToDefenseJury}
                    exact
                />
                <Route
                    path={`${path}/thesis_defense_plan/:id/defense_jury/:juryId/reassign`}
                    component={EditTeacherAndThesisToDefenseJury}
                    exact
                />
                {/*<Route
                    path={`${path}/thesis_defense_plan/:id/assign-automatically`}
                    component={AssignTeacherAndThesisAutomatically}
                    exact
                />*/}
                <Route
                    path={`${path}/topic`}
                    component={JuryTopicList}
                    exact
                />

                <Route
                    path={`${path}/teacher/assigned`}
                    exact
                    component={AssignedThesisDefensePlan}
                />
                <Route
                    path={`${path}/teacher/assigned/:id`}
                    component={AssignedDefenseJury}
                    exact
                />
                <Route
                    path={`${path}/teacher/assigned/:id/defense_jury/:juryId`}
                    component={DefenseJuryDetail}
                    exact
                />
                <Route
                    path={`${path}/teacher/superviser`}
                    component={SuperviseThesisList}
                    exact
                />
                <Route
                    path={`${path}/teacher/superviser/assign/:id`}
                    exact
                    component={AssignSupervisedThesis}
                />
                <Route
                    path={`${path}/teacher/president`}
                    exact
                    component={PresidentAssignedThesisDefensePlan}
                />
                <Route
                    path={`${path}/teacher/president/:id`}
                    exact
                    component={PresidentAssignedDefenseJury}
                />
                <Route
                    path={`${path}/teacher/president/:id/defense_jury/:juryId`}
                    exact
                    component={DefenseJuryDetail}
                />
                <Route
                    path={`${path}/teacher/president/:id/defense_jury/:juryId/assign`}
                    exact
                    component={PresidentAssignJuryDefense}
                />
                <Route
                    path={`${path}/student/thesis_defense_plan/assigned`}
                    exact
                    component={StudentAssignedDefenseJury}
                />
                <Route
                    path={`${path}/student/create`}
                    exact
                    component={StudentCreateThesis}
                />
                <Route
                    path={`${path}/student/thesis_defense_plan/assigned/:id/defense_jury/:juryId`}
                    exact
                    component={DefenseJuryDetail}
                />
            </Switch>
        </>

    )
}
export default ThesisDefensePlanAssignmentRouter;