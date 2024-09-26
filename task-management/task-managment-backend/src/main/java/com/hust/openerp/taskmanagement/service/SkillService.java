package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkillService {
    List<Skill> getAllSkills();

    Skill create(Skill skill);

    void delete(String skillId);
}
