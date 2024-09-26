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
import com.hust.openerp.taskmanagement.entity.TaskCategory;
import com.hust.openerp.taskmanagement.service.TaskCategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task-categories")
@Tag(name = "Task Category", description = "APIs for task category management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskCategoryController {
    private final TaskCategoryService taskCategoryService;

    @GetMapping
    public List<TaskCategory> getAllTaskCategories() {
        return taskCategoryService.getAllTaskCategory();
    }

    @PostMapping
    public TaskCategory createTaskCategory(@RequestBody TaskCategory taskCategory) {
        return taskCategoryService.create(taskCategory);
    }

    @DeleteMapping("{id}")
    public void deleteTaskCategory(@PathVariable("id") String id) {
        taskCategoryService.delete(id);
    }
}
