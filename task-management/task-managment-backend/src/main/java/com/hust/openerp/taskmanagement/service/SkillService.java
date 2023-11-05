package com.hust.openerp.taskmanagement.service;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.Skill;

import java.util.List;

@Service
public interface SkillService {
    List<Skill> getAllSkills();

    Skill create(Skill skill);

    void delete(String skillId);
}
