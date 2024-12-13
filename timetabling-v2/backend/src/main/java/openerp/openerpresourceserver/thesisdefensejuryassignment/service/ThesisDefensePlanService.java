package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDefensePlanDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.AssignTeacherAndThesisToDefenseJuryIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateThesisDefensePlanIM;

import java.util.List;

public interface ThesisDefensePlanService {
    List<ThesisDefensePlanDTO> getAllThesisDefensePlan();

    ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlanIM graduationTerm);

    ThesisDefensePlan getThesisDefensePlanById(String id);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherWithId(String teacherId);

    List<DefenseJuryDTO> getThesisDefensePlanAssignedForTeacherWithTeacherId(String teacherId, String thesisDefensePlanId);

    List<DefenseJuryDTO> getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(String teacherId, String thesisDefensePlanId);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(String teacherId);

    ThesisDefensePlan updateThesisDefensePlan(String id, UpdateThesisDefensePlanIM graduationTerm);
}
