package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.AssignTeacherAndThesisToDefenseJuryIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateThesisDefensePlanIM;

import java.util.List;

public interface ThesisDefensePlanService {
    List<ThesisDefensePlan> getAllThesisDefensePlan();

    ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlanIM graduationTerm);

    ThesisDefensePlan getThesisDefensePlanById(String id);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherWithId(String teacherId);

    ThesisDefensePlan getThesisDefensePlanAssignedForTeacherWithTeacherId(String teacherId, String thesisDefensePlanId);

    ThesisDefensePlan getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(String teacherId, String thesisDefensePlanId);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(String teacherId);

    ThesisDefensePlan updateThesisDefensePlan(String id, UpdateThesisDefensePlanIM graduationTerm);
}
