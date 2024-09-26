package com.hust.openerp.taskmanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.entity.TaskPriority;
import com.hust.openerp.taskmanagement.service.TaskPriorityService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

// TODO: check if user has permission to delete this task priority

@RestController
@RequestMapping("/task-priorities")
@Tag(name = "Task Priority", description = "APIs for task priority management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskPriorityController {
    private final TaskPriorityService taskPriorityService;

    @GetMapping
    public List<TaskPriority> getAllTaskPriorities() {
        return taskPriorityService.getAll();
    }

    @PostMapping
    public TaskPriority createTaskPriority(@RequestBody TaskPriority taskPriority) {
        return taskPriorityService.create(taskPriority);
    }

    @DeleteMapping("{id}")
    public void deleteTaskPriority(@PathVariable("id") String id) {
        taskPriorityService.delete(id);
    }
}
