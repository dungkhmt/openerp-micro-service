package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
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
import com.hust.openerp.taskmanagement.dto.ProjectDTO;
import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.form.SuggestForm;
import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskLog;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.service.TaskLogService;
import com.hust.openerp.taskmanagement.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task", description = "APIs for task management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskController {
    private final ModelMapper modelMapper;
    private final TaskService taskService;
    private final TaskLogService taskLogService;

    @GetMapping
    public PaginationDTO<TaskDTO> getPaginatedTasks(Pageable pageable, @RequestParam("projectId") UUID projectId,
            @Nullable @RequestParam(name = "q", required = false) String q) {
        var pageResult = taskService.getTasksOfProject(pageable, projectId, q)
                .map(this::convertToDto);
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
            @RequestBody TaskForm taskForm) {
        String userId = principal.getName();
        var task = taskService.createTask(taskForm, userId);
        return modelMapper.map(task, TaskDTO.class);
    }

    @GetMapping("/assigned-me")
    public PaginationDTO<TaskDTO> getPaginatedTasksAssignedToMe(
            Principal principal,
            Pageable pageable, @RequestParam(value = "search", required = false) String search) {
        String assignee = principal.getName();
        var tasks = taskService.getTasksAssignedToUser(pageable, assignee, search);
        var result = tasks.map(entity -> {
            var dto = modelMapper.map(entity, TaskDTO.class);
            dto.setProject(modelMapper.map(entity.getProject(), ProjectDTO.class));
            return dto;
        });
        return new PaginationDTO<>(result);
    }

    @GetMapping("{id}")
    public TaskDTO getTaskById(@PathVariable("id") UUID id) {
        // TODO: check if user has permission to get this task
        var task = taskService.getTask(id);
        var dto = convertToDto(task);
        dto.setHierarchies(taskService.getTaskHierarchyByRoot(task.getAncestorId()));
        return dto;
    }

    @GetMapping("{id}/logs")
    public List<TaskLog> getTaskLogs(@PathVariable("id") UUID id) {
        return taskLogService.getLogsByTaskId(id);
    }

    @PutMapping("{id}")
    public TaskDTO updateTask(Principal principal, @PathVariable("id") UUID id, @RequestBody TaskForm taskForm) {
        // TODO: check if user has permission to update this task
        var task = taskService.updateTask(id, taskForm, principal.getName());
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        return convertToDto(task);
    }

    @PostMapping("/suggest-assign-task")
    public List<User> suggestAssignTask(@RequestBody SuggestForm suggestForm) {
        return taskService.suggestAssignTask(suggestForm.getProjectId(), suggestForm.getSkillIds());
    }

    private TaskDTO convertToDto(Task task) {
        var dto = modelMapper.map(task, TaskDTO.class);
        return dto;
    }
}
