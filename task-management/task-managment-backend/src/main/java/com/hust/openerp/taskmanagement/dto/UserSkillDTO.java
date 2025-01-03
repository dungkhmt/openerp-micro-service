package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSkillDTO {
	private User user;
    private Skill skill;
}
