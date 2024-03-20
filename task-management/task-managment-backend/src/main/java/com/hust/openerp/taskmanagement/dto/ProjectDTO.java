package com.hust.openerp.taskmanagement.dto;

import java.util.UUID;

import com.hust.openerp.taskmanagement.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProjectDTO {
  private UUID id;

  private String name;

  private String code;

  private String description;

  private String createdStamp;

  private String lastUpdatedStamp;

  private User creator;

  private long taskCount;
}
