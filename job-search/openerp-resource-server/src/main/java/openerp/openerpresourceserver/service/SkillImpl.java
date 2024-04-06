package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.Skill;
import openerp.openerpresourceserver.repo.SkillRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class SkillImpl implements SkillService {
    SkillRepo skillRepo;

    @Override
    public List<Skill> getAllByUserId(String userId) {
        return skillRepo.findByUserId(userId);
    }

    @Override
    public Skill getById(Integer id) {
        Optional<Skill> result = skillRepo.findById(id);
        Skill skill = null;
        if(result.isPresent()) {
            skill = result.get();
        }
        else {
            throw new RuntimeException("Skill id not found: " +  id);
        }
        return skill;
    }

    @Override
    public Skill save(Skill skill) {
        return skillRepo.save(skill);
    }

    @Override
    public void deleteById(Integer id) {
        skillRepo.deleteById(id);
    }
}
