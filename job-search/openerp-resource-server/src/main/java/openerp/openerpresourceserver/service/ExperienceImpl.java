package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.entity.Experience;
import openerp.openerpresourceserver.repo.ExperienceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ExperienceImpl implements ExperienceService{
    ExperienceRepo experienceRepo;
    @Override
    public List<Experience> getAllByUserId(String id) {
        return experienceRepo.findByUserId(id);
    }

    @Override
    public Experience getById(Integer id) {
        Optional<Experience> result = experienceRepo.findById(id);
        Experience experience = null;
        if(result.isPresent()) {
            experience = result.get();
        }
        else {
            throw new RuntimeException("Experience id not found: " +  id);
        }
        return experience;
    }

    @Override
    public Experience save(Experience experience) {
        return experienceRepo.save(experience);
    }

    @Override
    public void deleteById(Integer id) {
        experienceRepo.deleteById(id);
    }
}
