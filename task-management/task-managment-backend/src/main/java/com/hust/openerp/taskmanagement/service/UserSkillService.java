package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.UserSkillDTO;

@Service
public interface UserSkillService {
	
    List<UserSkillDTO> getUserSkills(String userId);
    
    void updateUserSkills(String userId, List<String> skillList);

}
