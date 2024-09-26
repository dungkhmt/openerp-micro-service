package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVEducation;
import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.repo.CVWorkingExperienceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CVWorkingExperienceImpl implements CVWorkingExperienceService{
    CVWorkingExperienceRepo cvWorkingExperienceRepo;
    @Override
    public List<CVWorkingExperience> getAll() {
        return cvWorkingExperienceRepo.findAll();
    }

    @Override
    public List<CVWorkingExperience> getAllByCVId(Integer cvId) {
        return cvWorkingExperienceRepo.findBycvId(cvId);
    }

    @Override
    public CVWorkingExperience getById(Integer id) {
        Optional<CVWorkingExperience> result = cvWorkingExperienceRepo.findById(id);
        CVWorkingExperience cvWorkingExperience = null;
        if(result.isPresent()) {
            cvWorkingExperience = result.get();
        }
        else {
            throw new RuntimeException("CVWorkingExperience id not found: " +  id);
        }
        return cvWorkingExperience;
    }

    @Override
    public CVWorkingExperience save(CVWorkingExperience cvWorkingExperience) {
        return cvWorkingExperienceRepo.save(cvWorkingExperience);
    }

    @Override
    public void deleteById(Integer id) {
        cvWorkingExperienceRepo.deleteById(id);
    }
}
