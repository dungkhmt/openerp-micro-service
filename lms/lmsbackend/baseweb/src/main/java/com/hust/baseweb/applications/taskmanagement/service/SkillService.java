package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.entity.Skill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SkillService {
    List<Skill> getAllSkills();

    Skill create(Skill skill);

    void delete(String skillId);
}
