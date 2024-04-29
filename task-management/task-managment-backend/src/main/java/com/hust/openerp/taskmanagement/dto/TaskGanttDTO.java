package com.hust.openerp.taskmanagement.dto;

import lombok.RequiredArgsConstructor;

import lombok.Data;
import java.util.UUID;
import java.util.Date;

import com.hust.openerp.taskmanagement.entity.User;

@Data
@RequiredArgsConstructor
public class TaskGanttDTO {
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
  private User assignee;
  private UUID parentId;
}
