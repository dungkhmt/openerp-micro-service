package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisDefensePlanIM;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.ThesisDefensePlanRepo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Service

public class ThesisDefensePlanServiceImpl implements ThesisDefensePlanService {


    private ThesisDefensePlanRepo graduationTermRepo;

    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlan() {
        List <ThesisDefensePlan> thesisDefensePlanList = graduationTermRepo.findAll();
        Collections.reverse(thesisDefensePlanList);
        return  thesisDefensePlanList;
    }

    @Override
    public ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlanIM graduationTerm) {
        ThesisDefensePlan thesisDefensePlan = new ThesisDefensePlan(
                graduationTerm.getId(),
                graduationTerm.getName(),
                graduationTerm.getDescription(),
                graduationTerm.getSemester(),
                graduationTerm.getStartDate(),
                graduationTerm.getEndDate()
        );
        return graduationTermRepo.save(thesisDefensePlan);
    }

    @Override
    public ThesisDefensePlan getThesisDefensePlanById(String id) {
        ThesisDefensePlan foundDefensePlan = graduationTermRepo.findById(id).orElse(null);
        return foundDefensePlan;
    }

    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherWithId(String teacherId) {
        List<ThesisDefensePlan> thesisDefensePlanList = graduationTermRepo.findAll();
        //        List<DefenseJury> filteredList = defenseJuryList.stream()
//                .filter(defenseJury -> defenseJury.getDefenseJuryTeacherRoles().stream()
//                        .anyMatch(role -> role.getTeacher().getId().equals(teacherIdToFilter)))
//                .collect(Collectors.toList());

        return thesisDefensePlanList.stream().filter(
                        thesisDefensePlan -> thesisDefensePlan
                                .getDefenseJuries()
                                .stream()
                                .anyMatch(
                                        defenseJury -> defenseJury
                                                .getDefenseJuryTeacherRoles()
                                                .stream()
                                                .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                                        .getTeacher()
                                                        .getId()
                                                        .equals(teacherId))))
                .toList();
    }

    @Override
    public ThesisDefensePlan getThesisDefensePlanAssignedForTeacherWithTeacherId(String teacherId, String thesisDefensePlanId) {
        ThesisDefensePlan thesisDefensePlan = graduationTermRepo.findById(thesisDefensePlanId).orElse(null);
        List<DefenseJury> defenseJuryList = thesisDefensePlan.getDefenseJuries();
        thesisDefensePlan.setDefenseJuries(
                defenseJuryList
                        .stream()
                        .filter(defenseJury -> defenseJury
                                .getDefenseJuryTeacherRoles()
                                .stream()
                                .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                        .getTeacher()
                                        .getId()
                                        .equals(teacherId)))
                        .toList()
        );
        return thesisDefensePlan;
    }

    @Override
    public ThesisDefensePlan getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(String teacherId, String thesisDefensePlanId) {
        int PRESIDENT_ROLE = 2;
        ThesisDefensePlan thesisDefensePlan = graduationTermRepo.findById(thesisDefensePlanId).orElse(null);
        List<DefenseJury> defenseJuryList = thesisDefensePlan.getDefenseJuries();
        thesisDefensePlan.setDefenseJuries(
                defenseJuryList
                        .stream()
                        .filter(defenseJury -> defenseJury
                                .getDefenseJuryTeacherRoles()
                                .stream()
                                .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                        .getTeacher()
                                        .getId()
                                        .equals(teacherId) && defenseJuryTeacherRole.getRole().getId() == PRESIDENT_ROLE))
                        .toList()
        );
        return thesisDefensePlan;
    }

    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(String teacherId) {
        int PRESIDENT_ROLE = 2;
        List<ThesisDefensePlan> thesisDefensePlanList = graduationTermRepo.findAll();
        return thesisDefensePlanList.stream().filter(
                        thesisDefensePlan -> thesisDefensePlan
                                .getDefenseJuries()
                                .stream()
                                .anyMatch(
                                        defenseJury -> defenseJury
                                                .getDefenseJuryTeacherRoles()
                                                .stream()
                                                .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                                        .getTeacher()
                                                        .getId()
                                                        .equals(teacherId) && defenseJuryTeacherRole.getRole().getId() == PRESIDENT_ROLE)))
                .toList();

    }

    @Override
    public ThesisDefensePlan updateThesisDefensePlan(String id, UpdateThesisDefensePlanIM graduationTerm) {
        ThesisDefensePlan thesisDefensePlan = graduationTermRepo.findById(id).orElse(null);
        if (thesisDefensePlan == null) return null;
        thesisDefensePlan.setId(graduationTerm.getId());
        thesisDefensePlan.setDescription(graduationTerm.getDescription());
        thesisDefensePlan.setName(graduationTerm.getName());
        thesisDefensePlan.setEndDate(graduationTerm.getEndDate());
        thesisDefensePlan.setStartDate(graduationTerm.getStartDate());
        thesisDefensePlan.setSemester(graduationTerm.getSemester());
        return graduationTermRepo.save(thesisDefensePlan);
    }

}
