package com.hust.openerp.taskmanagement.service.implement;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.repository.SkillRepository;
import com.hust.openerp.taskmanagement.service.SkillService;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SkillServiceImplement implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Skill create(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public void delete(String skillId) {
        skillRepository.deleteById(skillId);
    }
}
