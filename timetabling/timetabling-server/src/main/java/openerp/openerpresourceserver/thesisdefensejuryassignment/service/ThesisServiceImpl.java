package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisModel;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.*;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
    private static ThesisDTO getThesisDTO(Thesis thesis) {
        ThesisDTO thesisDTO = new ThesisDTO();
        thesisDTO.setId(thesis.getId());
        thesisDTO.setThesisName(thesis.getThesisName());
        thesisDTO.setThesisAbstract(thesis.getThesisAbstract());
        thesisDTO.setSupervisor(thesis.getSupervisor().getTeacherName());
        thesisDTO.setThesisDefensePlanId(thesis.getThesisDefensePlan().getId());
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