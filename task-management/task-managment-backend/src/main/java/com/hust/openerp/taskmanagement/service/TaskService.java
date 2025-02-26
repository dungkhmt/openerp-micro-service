package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateTaskForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateTaskForm;

import jakarta.annotation.Nullable;

@Service
public interface TaskService {
    TaskDTO createTask(CreateTaskForm taskForm, String creatorId);

    TaskDTO getTask(UUID taskId, String getterId);

    TaskDTO updateTask(UUID taskId, UpdateTaskForm taskForm, String updateBy);

    Page<TaskDTO> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search);

    Page<TaskDTO> getTasksOfProject(Pageable pageable, UUID projectId, @Nullable String search, String getterId);

    List<TaskGanttDTO> getTaskGantt(UUID projectId, String from, String to, String q);

    Page<TaskDTO> getTasksCreatedByUser(Pageable pageable, String creator, @Nullable String search);
    
    List<TaskDTO> getTasksForMemberInProject (UUID projectId, String assigneeId);
    
    List<TaskDTO> getEventTasks(String userId, UUID eventId);
    
    void addExistingTasksToEvent(String userId, UUID eventId, List<UUID> taskIds);
    
    List<TaskDTO> getTasksWithoutEvent(String userId, UUID projectId);
}
