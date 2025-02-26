package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.hust.openerp.taskmanagement.entity.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TaskDTO {
  private UUID id;
  private String name;
  private String description;
  private String statusId;
  private String priorityId;
  private String categoryId;
  private Date createdDate;
  private Date fromDate;
  private Date dueDate;
  private Integer estimatedTime;
  private Integer progress;
  private String attachmentPaths;
  private Date createdStamp;
  private Date lastUpdatedStamp;
  private User creator;
  private ProjectDTO project;
  private EventDTO event;
  private User assignee;
  private List<TaskHierarchyDTO> hierarchies;
}
