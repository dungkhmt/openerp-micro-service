package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.dto.form.TaskStatusForm;
import com.hust.openerp.taskmanagement.entity.StatusItem;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;

@Service
public interface TaskService {
    Task createTask(Task task);

    List<Task> getAllTasks();

    List<Task> getAllTaskInProject(UUID projectId);

    List<Object[]> getTaskStaticsCategoryInProject(UUID projectId);

    List<Object[]> getTaskStaticsStatusInProject(UUID projectId);

    StatusItem getStatusItemByStatusId(String statusId);

    Task getTask(UUID taskId);

    Task updateTasks(Task task);

    Task updateStatusTask(UUID taskId, TaskStatusForm taskStatusForm, String userLoginId);

    Task updateTask(UUID taskId, TaskForm taskForm, String createdByUserLoginId);

    void addTaskSkill(UUID taskId, String skillId);

    List<User> suggestAssignTask(UUID projectId, List<String> skillIds);
}
