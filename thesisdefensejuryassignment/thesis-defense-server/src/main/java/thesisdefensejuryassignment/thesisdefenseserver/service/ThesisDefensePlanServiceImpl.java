package thesisdefensejuryassignment.thesisdefenseserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import thesisdefensejuryassignment.thesisdefenseserver.entity.TrainingProgram;
import thesisdefensejuryassignment.thesisdefenseserver.repo.ThesisDefensePlanRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service

public class ThesisDefensePlanServiceImpl implements ThesisDefensePlanService {


    private ThesisDefensePlanRepo graduationTermRepo;
    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlan() {

        return graduationTermRepo.findAll();
    }

    @Override
    public ThesisDefensePlan createThesisDefensePlan(ThesisDefensePlan graduationTerm) {
        return graduationTermRepo.save(graduationTerm);
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


}
