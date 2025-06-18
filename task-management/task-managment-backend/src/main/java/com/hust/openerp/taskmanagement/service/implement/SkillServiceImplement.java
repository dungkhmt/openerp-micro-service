package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hust.openerp.taskmanagement.dto.SkillDTO;
import com.hust.openerp.taskmanagement.dto.form.SkillForm;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Override
    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAllByOrderByCreatedStampDesc().
            stream().map(x -> modelMapper.map(x, SkillDTO.class)).
            collect(Collectors.toList());
    }

    @Override
    public SkillDTO create(SkillForm form) {
    	if (skillRepository.existsByCode(form.getCode())) {
    		throw new ApiException(ErrorCode.SKILL_EXIST);
        }

        Skill skill = new Skill();
        skill.setSkillId(UUID.randomUUID());
        skill.setCode(form.getCode());
        skill.setName(form.getName());
        skill.setDescription(form.getDescription());

        return modelMapper.map(skillRepository.save(skill), SkillDTO.class);
    }

    @Override
    @Transactional
    public void delete(UUID skillId) {
    	userSkillRepository.deleteBySkillId(skillId);

        taskSkillRepository.deleteBySkillId(skillId);

    	skillRepository.deleteById(skillId);
    }

	@Override
	public void update(UUID id, Skill updatedSkill) {
		Skill skill = skillRepository.findById(id).orElseThrow(
				() -> new ApiException(ErrorCode.SKILL_NOT_FOUND));

		skill.setDescription(updatedSkill.getDescription());
		skillRepository.save(skill);
	}
}
