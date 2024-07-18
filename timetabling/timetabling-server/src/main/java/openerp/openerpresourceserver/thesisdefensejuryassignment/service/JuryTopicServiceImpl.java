package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDefensePlanDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.JuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateJuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.AcademicKeywordRepo;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.JuryTopicRepo;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.TeacherRepo;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.ThesisDefensePlanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
@Log4j2
@AllArgsConstructor
@Service
public class JuryTopicServiceImpl implements JuryTopicService{
    @Autowired
    private JuryTopicRepo juryTopicRepo;
    @Autowired
    private AcademicKeywordRepo academicKeywordRepo;
    @Autowired
    private ThesisDefensePlanRepo thesisDefensePlanRepo;
    @Autowired
    private TeacherRepo teacherRepo;
    @Override
    public List<JuryTopic> getAll() {
        return juryTopicRepo.findAll();
    }

    @Override
    public List<JuryTopic> getAllByThesisDefensePlanId (String thesisDefensePlanId) {
        return juryTopicRepo.findByThesisDefensePlanId(thesisDefensePlanId);
    }
    @Override
    public List<ThesisDefensePlanDTO> getAllThesisDefensePlan() {
        List <ThesisDefensePlan> thesisDefensePlanList = thesisDefensePlanRepo.findAll();
        Collections.reverse(thesisDefensePlanList);
        List<ThesisDefensePlanDTO> thesisDefensePlanDTOList = new LinkedList<>();
        for (ThesisDefensePlan thesisDefensePlan: thesisDefensePlanList){
            ThesisDefensePlanDTO thesisDefensePlanDTO = new ThesisDefensePlanDTO(thesisDefensePlan.getId(),thesisDefensePlan.getName(),thesisDefensePlan.getDescription(), thesisDefensePlan.getSemester(),thesisDefensePlan.getStartDate(), thesisDefensePlan.getEndDate());
            thesisDefensePlanDTOList.add(thesisDefensePlanDTO);
        }
        return  thesisDefensePlanDTOList;
    }
    @Override
    public String createJuryTopic(JuryTopicIM juryTopicIM) {
        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (String keywordId: juryTopicIM.getAcademicKeywordList() ){
            AcademicKeyword keyword = academicKeywordRepo.findById(keywordId).orElse(null);
            if (keyword == null) return "ERROR";
            academicKeywordList.add(keyword);
        }
        Teacher teacher = teacherRepo.findById(juryTopicIM.getTeacherId()).orElse(null);
        if (teacher == null) return null;
        JuryTopic juryTopic = new JuryTopic(juryTopicIM.getName(), academicKeywordList, teacher);
        ThesisDefensePlan thesisDefensePlan = thesisDefensePlanRepo.findById(juryTopicIM.getThesisDefensePlanId()).orElse(null);
        if (thesisDefensePlan == null) return null;
        juryTopic.setThesisDefensePlan(thesisDefensePlan);
        JuryTopic savedJuryTopic = juryTopicRepo.save(juryTopic);
        return "Tạo phân ban " + savedJuryTopic.getName() + " thành công";
    }

    @Override
    public String updateJuryTopic(int juryTopicId, UpdateJuryTopicIM updateJuryTopicIM){
        JuryTopic juryTopic = juryTopicRepo.findById(juryTopicId).orElse(null);
        if (juryTopic == null) return "ERROR";
        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (String academicKeywordId : updateJuryTopicIM.getAcademicKeywordList()){
            AcademicKeyword academicKeyword = academicKeywordRepo.findById(academicKeywordId).orElse(null);
            if (academicKeyword == null) return "ERROR";
            academicKeywordList.add(academicKeyword);
        }
        juryTopic.setAcademicKeywordList(academicKeywordList);
        JuryTopic saved = juryTopicRepo.save(juryTopic);
        return "Cập nhật phân ban thành công";
    }
}
