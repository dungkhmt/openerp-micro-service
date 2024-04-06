package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.entity.Education;
import openerp.openerpresourceserver.repo.EducationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EducationImpl implements EducationService{
    EducationRepo educationRepo;
    @Override
    public List<Education> getAllByUserId(String id) {
        return educationRepo.findByUserId(id);
    }

    @Override
    public Education getById(Integer id) {
        Optional<Education> result = educationRepo.findById(id);
        Education education = null;
        if(result.isPresent()) {
            education = result.get();
        }
        else {
            throw new RuntimeException("Education id not found: " +  id);
        }
        return education;
    }

    @Override
    public Education save(Education education) {
        return educationRepo.save(education);
    }

    @Override
    public void deleteById(Integer id) {
        educationRepo.deleteById(id);
    }
}
