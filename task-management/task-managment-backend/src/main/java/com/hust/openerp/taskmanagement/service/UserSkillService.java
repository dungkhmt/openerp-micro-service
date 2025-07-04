package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.UserSkillDTO;

@Service
public interface UserSkillService {
	
    List<UserSkillDTO> getMySkills(String userId);
    
    void updateMySkills(String userId, List<UUID> skillList);

}
