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
import com.hust.openerp.taskmanagement.entity.Status;
import com.hust.openerp.taskmanagement.service.StatusService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statuses")
@Tag(name = "Status", description = "APIs for task status management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class StatusController {
    private final StatusService statusService;

    @GetMapping
    public List<Status> getAllStatus() {
        return statusService.getAllStatus();
    }
    
    @PostMapping
    public Status createStatus(@RequestBody Status status) {
        return statusService.create(status);
    }

    @DeleteMapping("{id}")
    public void deleteStatus(@PathVariable("id") String id) {
    	statusService.delete(id);
    }
}
