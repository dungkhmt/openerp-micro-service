package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskHierarchyDTO {
  private UUID id;
  private String name;
  private Integer level;
  private String statusId;
  private Integer progress;
  private String assigneeId;
  private Date fromDate;
}
