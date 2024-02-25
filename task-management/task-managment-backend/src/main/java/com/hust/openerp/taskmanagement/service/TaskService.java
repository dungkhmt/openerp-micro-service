package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.dto.form.TaskStatusForm;
import com.hust.openerp.taskmanagement.entity.StatusItem;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;

import jakarta.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    Page<Task> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search);
}
