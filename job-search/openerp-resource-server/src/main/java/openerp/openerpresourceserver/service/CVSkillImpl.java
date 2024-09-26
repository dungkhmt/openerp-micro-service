package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVSkill;
import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.repo.CVSkillRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CVSkillImpl implements CVSkillService{
    CVSkillRepo cvSkillRepo;

    @Override
    public List<CVSkill> getAll() {
        return cvSkillRepo.findAll();
    }

    @Override
    public List<CVSkill> getAllByCVId(Integer cvId) {
        return cvSkillRepo.findBycvId(cvId);
    }

    @Override
    public CVSkill getById(Integer id) {
        Optional<CVSkill> result = cvSkillRepo.findById(id);
        CVSkill cvSkill = null;
        if(result.isPresent()) {
            cvSkill = result.get();
        }
        else {
            throw new RuntimeException("CVWorkingExperience id not found: " +  id);
        }
        return cvSkill;
    }

    @Override
    public CVSkill save(CVSkill cvSkill) {
        return cvSkillRepo.save(cvSkill);
    }

    @Override
    public void deleteById(Integer id) {
        cvSkillRepo.deleteById(id);
    }
}
