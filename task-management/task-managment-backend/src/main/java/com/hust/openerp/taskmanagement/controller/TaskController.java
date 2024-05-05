package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.PaginationDTO;
import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateTaskForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateTaskForm;
import com.hust.openerp.taskmanagement.entity.TaskLog;
import com.hust.openerp.taskmanagement.service.TaskLogService;
import com.hust.openerp.taskmanagement.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task", description = "APIs for task management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskLogService taskLogService;

    @GetMapping
    public PaginationDTO<TaskDTO> getPaginatedTasks(Principal principal, Pageable pageable,
            @RequestParam("projectId") UUID projectId,
            @Nullable @RequestParam(name = "q", required = false) String q) {
        var pageResult = taskService.getTasksOfProject(pageable, projectId, q, principal.getName());
        return new PaginationDTO<>(pageResult);
    }

    @GetMapping("/gantt")
    public List<TaskGanttDTO> getTasksForGanttChart(@RequestParam("projectId") UUID projectId,
            @RequestParam("from") String from, @RequestParam("to") String to,
            @RequestParam(name = "q", required = false) String q) {
        return taskService.getTaskGantt(projectId, from, to, q);
    }

    @PostMapping
    public TaskDTO createNewTask(
            Principal principal,
            @RequestBody @Valid CreateTaskForm taskForm) {
        String userId = principal.getName();
        return taskService.createTask(taskForm, userId);
    }

    @GetMapping("/assigned-me")
    public PaginationDTO<TaskDTO> getPaginatedTasksAssignedToMe(
            Principal principal,
            Pageable pageable, @RequestParam(value = "search", required = false) String search) {
        String assignee = principal.getName();
        var tasksDto = taskService.getTasksAssignedToUser(pageable, assignee, search);
        return new PaginationDTO<>(tasksDto);
    }

    @GetMapping("{id}")
    public TaskDTO getTaskById(Principal principal, @PathVariable("id") UUID id) {
        return taskService.getTask(id, principal.getName());
    }

    @GetMapping("{id}/logs")
    public List<TaskLog> getTaskLogs(@PathVariable("id") UUID id) {
        return taskLogService.getLogsByTaskId(id);
    }

    @PutMapping("{id}")
    public TaskDTO updateTask(Principal principal, @PathVariable("id") UUID id,
            @RequestBody @Valid UpdateTaskForm taskForm) {
        return taskService.updateTask(id, taskForm, principal.getName());
    }
}
