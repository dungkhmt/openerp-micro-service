package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.TaskStatisticByStatusDTO;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByStatusRequest;
import com.hust.openerp.taskmanagement.dto.request.TaskStatisticByWorkloadRequest;
import com.hust.openerp.taskmanagement.service.TaskStatisticService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task-statistics")
@Tag(name = "Task Statistics", description = "The Task Statistics API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskStatisticController {
    private final TaskStatisticService taskStatisticService;

    @GetMapping("/by-status")
    public TaskStatisticByStatusDTO getTaskStatisticsByStatus(Principal principal,
            @Valid TaskStatisticByStatusRequest request) {
        return taskStatisticService.getTaskStatisticByStatus(principal.getName(), request);
    }

    @GetMapping("/by-workload")
    public List<TaskStatisticByStatusDTO> getTaskStatisticsByWorkload(Principal principal,
            @Valid TaskStatisticByWorkloadRequest request) {
        return taskStatisticService.getTaskStatisticWorkloadByStatus(principal.getName(), request);
    }
}