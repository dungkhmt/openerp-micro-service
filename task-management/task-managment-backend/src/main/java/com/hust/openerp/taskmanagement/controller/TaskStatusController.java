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
import com.hust.openerp.taskmanagement.entity.TaskStatus;
import com.hust.openerp.taskmanagement.service.TaskStatusService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task-statuses")
@Tag(name = "Task Status", description = "APIs for task status management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskStatusController {
    private final TaskStatusService taskStatusService;

    @GetMapping
    public List<TaskStatus> getListTaskStatus() {
        return taskStatusService.getAllTaskStatus();
    }
    
    @PostMapping
    public TaskStatus createTaskStatus(@RequestBody TaskStatus taskStatus) {
        return taskStatusService.create(taskStatus);
    }

    @DeleteMapping("{id}")
    public void deleteTaskStatus(@PathVariable("id") String id) {
    	taskStatusService.delete(id);
    }
}
