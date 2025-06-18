package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.UserSkillDTO;
import com.hust.openerp.taskmanagement.entity.UserSkill;
import com.hust.openerp.taskmanagement.repository.UserSkillRepository;
import com.hust.openerp.taskmanagement.service.UserSkillService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserSkillServiceImplement implements UserSkillService {
	private final ModelMapper modelMapper;
	
	private final UserSkillRepository userSkillRepository;

	@Override
	public List<UserSkillDTO> getMySkills(String userId) {
      var entities = userSkillRepository.findByUserIdWithSkill(userId);
      return entities.stream().map(entity -> modelMapper.map(entity, UserSkillDTO.class)).toList();
	}

	@Override
	@Transactional
	public void updateMySkills(String userId, List<UUID> skillList) {
		userSkillRepository.deleteByUserId(userId);
		
		for (UUID skillId : skillList) {
            UserSkill userSkill = new UserSkill();
            userSkill.setUserId(userId);
            userSkill.setSkillId(skillId);
            userSkillRepository.save(userSkill);
        }
	}
}
