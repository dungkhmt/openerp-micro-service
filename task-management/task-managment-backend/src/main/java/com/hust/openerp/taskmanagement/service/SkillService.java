package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.SkillDTO;
import com.hust.openerp.taskmanagement.dto.form.SkillForm;
import com.hust.openerp.taskmanagement.entity.Skill;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface SkillService {
    List<SkillDTO> getAllSkills();

    SkillDTO create(SkillForm skill);

    void delete(UUID skillId);

	void update(UUID id, Skill updatedSkill);
}
