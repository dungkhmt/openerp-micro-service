package thesisdefensejuryassignment.thesisdefenseserver.service;

import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignTeacherAndThesisToDefenseJuryIM;

import java.util.List;

public interface ThesisDefensePlanService {
    List<ThesisDefensePlan> getAllThesisDefensePlan();

    ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlan graduationTerm);

    ThesisDefensePlan getThesisDefensePlanById(String id);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherWithId(String teacherId);

    ThesisDefensePlan getThesisDefensePlanAssignedForTeacherWithTeacherId(String teacherId, String thesisDefensePlanId);

    ThesisDefensePlan getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(String teacherId, String thesisDefensePlanId);

    List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(String teacherId);

}
