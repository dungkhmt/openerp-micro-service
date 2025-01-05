package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.Skill;

@Service
public interface TaskSkillService {
	
    List<Skill> getTaskSkills(UUID taskId, String userId);
    
    void updateTaskSkills(UUID taskId, List<String> skillIdList, String userId);

}
