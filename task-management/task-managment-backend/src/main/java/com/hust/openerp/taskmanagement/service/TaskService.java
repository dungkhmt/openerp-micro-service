package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.TaskHierarchyDTO;
import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;

import jakarta.annotation.Nullable;

@Service
public interface TaskService {
    Task createTask(TaskForm taskForm, String creatorId);

    List<Task> getAllTaskInProject(UUID projectId);

    List<Object[]> getTaskStaticsCategoryInProject(UUID projectId);

    List<Object[]> getTaskStaticsStatusInProject(UUID projectId);

    Task getTask(UUID taskId);

    Task updateTask(UUID taskId, TaskForm taskForm, String updateBy);

    void addTaskSkill(UUID taskId, String skillId);

    List<User> suggestAssignTask(UUID projectId, List<String> skillIds);

    Page<Task> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search);

    Page<Task> getTasksOfProject(Pageable pageable, UUID projectId, @Nullable String search);

    long countTasksByProjectId(UUID projectId);

    List<TaskHierarchyDTO> getTaskHierarchyByRoot(UUID ancestorId);

    List<TaskGanttDTO> getTaskGantt(UUID projectId, String from, String to, String q);
}
