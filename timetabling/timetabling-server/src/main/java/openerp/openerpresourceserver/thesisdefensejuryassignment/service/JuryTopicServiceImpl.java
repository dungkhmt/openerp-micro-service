package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.JuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.AcademicKeywordRepo;
import openerp.openerpresourceserver.thesisdefensejuryassignment.repo.JuryTopicRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<JuryTopic> getAll() {
        return juryTopicRepo.findAll();
    }

    @Override
    public JuryTopic getById(int id) {
        return juryTopicRepo.findById(id).orElse(null);
    }

    @Override
    public String createJuryTopic(JuryTopicIM juryTopicIM) {
        List<AcademicKeyword> academicKeywordList = new LinkedList<>();
        for (String keywordId: juryTopicIM.getAcademicKeywordList() ){
            AcademicKeyword keyword = academicKeywordRepo.findById(keywordId).orElse(null);
            if (keyword == null) return "ERROR";
            academicKeywordList.add(keyword);
        }
        JuryTopic juryTopic = new JuryTopic(juryTopicIM.getName(), academicKeywordList);
        JuryTopic savedJuryTopic = juryTopicRepo.save(juryTopic);
        return "Tạo phân ban " + savedJuryTopic.getName() + " thành công";
    }
}
