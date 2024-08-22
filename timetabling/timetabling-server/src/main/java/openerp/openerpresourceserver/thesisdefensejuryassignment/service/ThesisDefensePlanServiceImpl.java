package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.JuryTopicDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDefensePlanDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJurySession;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.DefenseJuryRepo;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.JuryTopicRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.ThesisDefensePlanRepo;

import java.util.*;

@Log4j2
@AllArgsConstructor
@Service

public class ThesisDefensePlanServiceImpl implements ThesisDefensePlanService {
    @Autowired
    private ThesisDefensePlanRepo graduationTermRepo;
    @Autowired
    private DefenseJuryRepo defenseJuryRepo;


    @Autowired
    private JuryTopicRepo juryTopicRepo;


    @Override
    public List<ThesisDefensePlanDTO> getAllThesisDefensePlan() {
        List <ThesisDefensePlan> thesisDefensePlanList = graduationTermRepo.findAll();
        Collections.reverse(thesisDefensePlanList);
        List<ThesisDefensePlanDTO> thesisDefensePlanDTOList = new LinkedList<>();
        for (ThesisDefensePlan thesisDefensePlan: thesisDefensePlanList){
            ThesisDefensePlanDTO thesisDefensePlanDTO = new ThesisDefensePlanDTO(thesisDefensePlan.getId(),thesisDefensePlan.getName(),thesisDefensePlan.getDescription(), thesisDefensePlan.getSemester(),thesisDefensePlan.getStartDate(), thesisDefensePlan.getEndDate());
            thesisDefensePlanDTOList.add(thesisDefensePlanDTO);
        }
        return  thesisDefensePlanDTOList;
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
        return thesisDefensePlanList.stream().filter(
                        thesisDefensePlan -> thesisDefensePlan
                                .getPlanTopicList()
                                .stream()
                                .anyMatch(planTopic -> planTopic.getDefenseJuryList()
                                        .stream()
                                        .anyMatch(
                                                defenseJury -> defenseJury
                                                        .getDefenseJuryTeacherRoles()
                                                        .stream()
                                                        .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                                                .getTeacher()
                                                                .getId()
                                                                .equals(teacherId))))
                )
                .toList();
    }
    @Override
    public List<DefenseJuryDTO> getThesisDefensePlanAssignedForTeacherWithTeacherId(String teacherId, String thesisDefensePlanId) {
        List<DefenseJury> defenseJuryList = defenseJuryRepo.findByPlanTopicThesisDefensePlanIdAndDefenseJuryTeacherRolesTeacherId(thesisDefensePlanId, teacherId);
        List<DefenseJuryDTO> defenseJuryDTOList = new ArrayList<>();
        for (DefenseJury defenseJury: defenseJuryList){
            DefenseJuryDTO defenseJuryDTO = new DefenseJuryDTO();
            defenseJuryDTO.setId(defenseJury.getId().toString());
            defenseJuryDTO.setName(defenseJury.getName());
            defenseJuryDTO.setMaxThesis(defenseJury.getMaxThesis());
            defenseJuryDTO.setDefenseDate(defenseJury.getDefenseDate());
            defenseJuryDTO.setDefenseRoom(defenseJury.getDefenseRoom());
            defenseJuryDTO.setDefenseSession(defenseJury.getDefenseJurySessionList().stream().map(DefenseJurySession::getDefenseSession).toList());
            JuryTopicDTO juryTopicDTO = new JuryTopicDTO(defenseJury.getPlanTopic().getName(), defenseJury.getPlanTopic().getAcademicKeywordList());
            defenseJuryDTO.setJuryTopicDTO(juryTopicDTO);
            defenseJuryDTOList.add(defenseJuryDTO);
        }
        return defenseJuryDTOList;
    }

    @Override
    public List<DefenseJuryDTO> getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(String teacherId, String thesisDefensePlanId) {
        int PRESIDENT_ROLE = 2;
        List<DefenseJury> defenseJuryList = defenseJuryRepo.findByPlanTopicThesisDefensePlanIdAndDefenseJuryTeacherRolesTeacherIdAndDefenseJuryTeacherRolesRoleId(thesisDefensePlanId,teacherId,PRESIDENT_ROLE);
        List<DefenseJuryDTO> defenseJuryDTOList = new ArrayList<>();
        for (DefenseJury defenseJury: defenseJuryList){
            DefenseJuryDTO defenseJuryDTO = new DefenseJuryDTO();
            defenseJuryDTO.setId(defenseJury.getId().toString());
            defenseJuryDTO.setName(defenseJury.getName());
            defenseJuryDTO.setMaxThesis(defenseJury.getMaxThesis());
            defenseJuryDTO.setDefenseDate(defenseJury.getDefenseDate());
            defenseJuryDTO.setDefenseRoom(defenseJury.getDefenseRoom());
            defenseJuryDTO.setDefenseSession(defenseJury.getDefenseJurySessionList().stream().map(DefenseJurySession::getDefenseSession).toList());
            defenseJuryDTO.setAssigned(defenseJury.isAssigned());
            JuryTopicDTO juryTopicDTO = new JuryTopicDTO(defenseJury.getPlanTopic().getName(), defenseJury.getPlanTopic().getAcademicKeywordList());
            defenseJuryDTO.setJuryTopicDTO(juryTopicDTO);
            defenseJuryDTOList.add(defenseJuryDTO);
        }
        return defenseJuryDTOList;
    }

    @Override
    public List<ThesisDefensePlan> getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(String teacherId) {
        int PRESIDENT_ROLE = 2;
        List<ThesisDefensePlan> thesisDefensePlanList = graduationTermRepo.findAll();
        return thesisDefensePlanList.stream().filter(
                thesisDefensePlan -> thesisDefensePlan
                        .getPlanTopicList()
                        .stream()
                        .anyMatch(defensePlanTopic -> defensePlanTopic.getDefenseJuryList()
                                .stream()
                                .anyMatch(
                                        defenseJury -> defenseJury
                                                .getDefenseJuryTeacherRoles()
                                                .stream()
                                                .anyMatch(defenseJuryTeacherRole -> defenseJuryTeacherRole
                                                        .getTeacher()
                                                        .getId()
                                                        .equals(teacherId) && defenseJuryTeacherRole.getRole().getId() == PRESIDENT_ROLE))
                        )
        ).toList();
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
