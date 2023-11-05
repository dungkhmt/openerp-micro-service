package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskForm {

  private String name;
  private String description;
  private Date dueDate;
  private String attachmentPaths;
  private UUID projectId;
  private String statusId;
  private String priorityId;
  private String categoryId;
  private String assigneeId;
  private List<String> skillIds;
}
