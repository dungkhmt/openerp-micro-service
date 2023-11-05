package com.hust.openerp.taskmanagement.dto.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskExecution;

@Getter
@Setter
public class TaskExecutionDao {

  private UUID id;
  private Task task;
  private String createdByUserLoginId;
  private String executionTags;
  private String comment;
  private List<Map<String, String>> changes;

  private Map<String, String> setChangeItem(String field, String value) {
    Map<String, String> temp = new HashMap<>();
    temp.put("field", field);
    temp.put("value", value);
    return temp;
  }

  public TaskExecutionDao(TaskExecution taskExecution) {
    this.setId(taskExecution.getId());
    this.setTask(taskExecution.getTask());
    this.setCreatedByUserLoginId(taskExecution.getCreatedByUserId());
    this.setExecutionTags(taskExecution.getExecutionTags());
    this.setComment(taskExecution.getComment());
    List<Map<String, String>> executionChanges = new ArrayList<>();
    if (taskExecution.getTaskName() != null) {
      executionChanges.add(this.setChangeItem("Tên nhiệm vụ", taskExecution.getTaskName()));
    }
    if (taskExecution.getTaskDescription() != null) {
      executionChanges.add(this.setChangeItem("Mô tả nhiệm vụ", taskExecution.getTaskDescription()));
    }
    if (taskExecution.getCategory() != null) {
      executionChanges.add(this.setChangeItem("Danh mục", taskExecution.getCategory()));
    }
    if (taskExecution.getPriority() != null) {
      executionChanges.add(this.setChangeItem("Độ ưu tiên", taskExecution.getPriority()));
    }
    if (taskExecution.getAssignee() != null) {
      executionChanges.add(this.setChangeItem("Gán cho", taskExecution.getAssignee()));
    }
    if (taskExecution.getStatus() != null) {
      executionChanges.add(this.setChangeItem("Trạng thái", taskExecution.getStatus()));
    }
    if (taskExecution.getDueDate() != null) {
      executionChanges.add(this.setChangeItem("Thời hạn", taskExecution.getDueDate()));
    }
    if (taskExecution.getFileName() != null) {
      executionChanges.add(this.setChangeItem("Tệp đính kèm", taskExecution.getFileName()));
    }
    this.setChanges(executionChanges);
  }
}
