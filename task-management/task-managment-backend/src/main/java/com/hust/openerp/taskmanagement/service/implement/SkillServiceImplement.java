package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.SkillRepository;
import com.hust.openerp.taskmanagement.repository.TaskSkillRepository;
import com.hust.openerp.taskmanagement.repository.UserSkillRepository;
import com.hust.openerp.taskmanagement.service.SkillService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SkillServiceImplement implements SkillService {

    private final SkillRepository skillRepository;
    
    private final UserSkillRepository userSkillRepository;
    
    private final TaskSkillRepository taskSkillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAllByOrderByCreatedStampDesc();
    }

    @Override
    public Skill create(Skill skill) {
    	if (skillRepository.existsById(skill.getSkillId())) {
    		throw new ApiException(ErrorCode.SKILL_EXIST);
        }
        
    	skillRepository.findByName(skill.getName()).ifPresent(e -> {
        	throw new ApiException(ErrorCode.SKILL_EXIST);
        });
    	
        return skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void delete(String skillId) {
    	userSkillRepository.deleteBySkillId(skillId);
    	
        taskSkillRepository.deleteBySkillId(skillId);
        
    	skillRepository.deleteById(skillId);
    }

	@Override
	public void update(String id, Skill updatedSkill) {
		Skill skill = skillRepository.findById(id).orElseThrow(
				() -> new ApiException(ErrorCode.SKILL_NOT_EXIST));
        
		skill.setDescription(updatedSkill.getDescription());
		skillRepository.save(skill);
	}
}
