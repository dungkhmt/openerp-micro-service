package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.TeacherSupervisedThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.AssignJuryTopicToThesisIM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisModel;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.*;

import javax.xml.crypto.Data;
import java.util.*;

@Log4j2
@AllArgsConstructor
@Service
public class ThesisServiceImpl implements ThesisService{
    @Autowired
    private ThesisDefensePlanRepo thesisDefensePlanRepo;

    @Autowired
    private TrainingProgramRepo trainingProgramRepo;

    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private AcademicKeywordRepo academicKeywordRepo;

    @Autowired
    private ThesisRepo thesisRepo;

    @Autowired
    private JuryTopicRepo juryTopicRepo;
    @Override
    public Thesis createNewThesis(ThesisModel thesisModel) {
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(thesisModel.getThesisDefensePlanId()).orElse(null);
        if (thesisDefensePlan == null) return null;
        Teacher teacher = teacherRepo.findById(thesisModel.getSupervisorId()).orElse(null);
        if (teacher == null) return null;
        TrainingProgram trainingProgram = trainingProgramRepo.findById(UUID.fromString(thesisModel.getProgramId())).orElse(null);
        if (trainingProgram == null) return null;
        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (String keyword: thesisModel.getThesisKeyword()){
            AcademicKeyword academicKeyword = academicKeywordRepo.findById(keyword).orElse(null);
            if (academicKeyword == null) return null;
            academicKeywordList.add(academicKeyword);
        }
        Thesis thesis = new Thesis(
                thesisModel.getThesisName(),
                thesisModel.getThesis_abstract(),
                thesisModel.getStudentName(),
                teacher,
                new Date(),
                academicKeywordList,
                thesisDefensePlan,
                trainingProgram,
                thesisModel.getStudentId(),
                thesisModel.getStudentEmail()
        );
        return thesisRepo.save(thesis);
    }

    @Override
    public List<TrainingProgram> getAllTrainingProgram() {
        return trainingProgramRepo.findAll();
    }

    @Override
    public List<ThesisDTO> getAllByStudentEmail(String studentEmail) {
        List<Thesis> thesisList = thesisRepo.findByStudentEmail(studentEmail).orElse(null);
        if (thesisList == null) return null;
        List<ThesisDTO> thesisDTOList = new LinkedList<>();
        for (Thesis thesis: thesisList){
            ThesisDTO thesisDTO = getThesisDTO(thesis);
            thesisDTOList.add(thesisDTO);
        }
        return thesisDTOList;
    }

    @Override
    public List<TeacherSupervisedThesisDTO> getAllThesisSupervisedByTeacher(String teacherId) {
        List<Thesis> foundThesisList = thesisRepo.findBySupervisorId(teacherId);
        List<TeacherSupervisedThesisDTO> results = new LinkedList<>();
        HashMap<String, List<Thesis>> planMappingThesis = new HashMap<>();
        for (Thesis thesis : foundThesisList){
            String thesisDefensePlanName = thesis.getThesisDefensePlan().getName();
            if (!planMappingThesis.containsKey(thesisDefensePlanName)){
                List<Thesis> thesisList = new LinkedList<>();
                thesisList.add(thesis);
                planMappingThesis.put(thesisDefensePlanName, thesisList);
            }
            else {
                planMappingThesis.get(thesisDefensePlanName).add(thesis);
            }
        }
        for (Map.Entry<String, List<Thesis>> item : planMappingThesis.entrySet()){
            String thesisDefensePlan = item.getKey();
            List<Thesis> thesisList = item.getValue();
            TeacherSupervisedThesisDTO supervisedThesis = new TeacherSupervisedThesisDTO(thesisDefensePlan, thesisList);
            results.add(supervisedThesis);
        }
        return results;
    }

    @Override
    public Thesis getById(String id) {
        return thesisRepo.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public String assignJuryTopicToThesis(String thesisId, AssignJuryTopicToThesisIM assignJuryTopicToThesisIM) {
        Thesis thesis = thesisRepo.findById(UUID.fromString(thesisId)).orElse(null);
        JuryTopic juryTopic = juryTopicRepo.findById(assignJuryTopicToThesisIM.getJuryTopicId()).orElse(null);
        if (thesis == null) return "ERROR";
        thesis.setJuryTopic(juryTopic);
        if (assignJuryTopicToThesisIM.getSecondJuryTopicId() != 0){
            JuryTopic secondJuryTopic = juryTopicRepo.findById(assignJuryTopicToThesisIM.getSecondJuryTopicId()).orElse(null);
            thesis.setSecondaryJuryTopic(secondJuryTopic);
        }
        Thesis savedThesis = thesisRepo.save(thesis);
        return "Phân ban cho đồ án " + savedThesis.getThesisName() + " thành công";
    }

    private static ThesisDTO getThesisDTO(Thesis thesis) {
        ThesisDTO thesisDTO = new ThesisDTO();
        thesisDTO.setId(thesis.getId());
        thesisDTO.setThesisName(thesis.getThesisName());
        thesisDTO.setThesisAbstract(thesis.getThesisAbstract());
        thesisDTO.setSupervisor(thesis.getSupervisor().getTeacherName());
        thesisDTO.setThesisDefensePlanId(thesis.getThesisDefensePlan().getId());
        if (thesis.getJuryTopic() != null){
            thesisDTO.setJuryTopicName(thesis.getJuryTopic().getName());
        }
        else {
            thesisDTO.setJuryTopicName(null);
        }
        if (thesis.getSecondaryJuryTopic() != null){
            thesisDTO.setSecondJuryTopicName(thesis.getSecondaryJuryTopic().getName());
        }
        else {
            thesisDTO.setSecondJuryTopicName(null);
        }
        if (thesis.getDefenseJury() != null){
            thesisDTO.setDefenseJuryId(thesis.getDefenseJury().getId());
            thesisDTO.setDefenseJuryName(thesis.getDefenseJury().getName());
        }
        else {
            thesisDTO.setDefenseJuryId(null);
            thesisDTO.setDefenseJuryName(null);
        }
        return thesisDTO;
    }
}
