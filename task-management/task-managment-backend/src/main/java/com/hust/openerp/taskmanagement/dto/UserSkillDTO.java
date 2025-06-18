package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.entity.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSkillDTO {
	private String userId;
    private Skill skill;
}
