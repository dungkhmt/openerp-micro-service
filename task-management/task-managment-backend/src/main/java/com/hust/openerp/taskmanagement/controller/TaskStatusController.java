package com.hust.openerp.taskmanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.entity.StatusItem;
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
    // private final TaskService taskService;

    @GetMapping
    public List<StatusItem> getListTaskStatus() {
        return taskStatusService.getAllTaskStatus();
    }

    // @PutMapping
    // public ResponseEntity<Object> updateStatusTask(
    // @RequestBody TaskStatusForm taskStatusForm,
    // Principal principal) {
    // String userLoginId = principal.getName();
    // taskService.updateStatusTask(taskStatusForm.getTaskId(), taskStatusForm,
    // userLoginId);
    // return ResponseEntity.ok().build();
    // }
}