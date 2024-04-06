package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.entity.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MemberUserDto {
  private User member;
  private String roleId;
}
