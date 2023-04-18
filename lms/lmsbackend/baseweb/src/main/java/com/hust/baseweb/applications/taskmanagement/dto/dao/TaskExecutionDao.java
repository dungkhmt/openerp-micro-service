package com.hust.baseweb.applications.taskmanagement.dto.dao;

import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.applications.taskmanagement.entity.TaskExecution;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
        this.setCreatedByUserLoginId(taskExecution.getCreatedByUserLoginId());
        this.setExecutionTags(taskExecution.getExecutionTags());
        this.setComment(taskExecution.getComment());
        List<Map<String, String>> changes = new ArrayList<>();
        if (taskExecution.getTaskName() != null) {
            changes.add(this.setChangeItem("Tên nhiệm vụ", taskExecution.getTaskName()));
        }
        if (taskExecution.getTaskDescription() != null) {
            changes.add(this.setChangeItem("Mô tả nhiệm vụ", taskExecution.getTaskDescription()));
        }
        if (taskExecution.getCategory() != null) {
            changes.add(this.setChangeItem("Danh mục", taskExecution.getCategory()));
        }
        if (taskExecution.getPriority() != null) {
            changes.add(this.setChangeItem("Độ ưu tiên", taskExecution.getPriority()));
        }
        if (taskExecution.getAssignee() != null) {
            changes.add(this.setChangeItem("Gán cho", taskExecution.getAssignee()));
        }
        if (taskExecution.getStatus() != null) {
            changes.add(this.setChangeItem("Trạng thái", taskExecution.getStatus()));
        }
        if (taskExecution.getDueDate() != null) {
            changes.add(this.setChangeItem("Thời hạn", taskExecution.getDueDate()));
        }
        if (taskExecution.getFileName() != null) {
            changes.add(this.setChangeItem("Tệp đính kèm", taskExecution.getFileName()));
        }
        this.setChanges(changes);
    }
}
