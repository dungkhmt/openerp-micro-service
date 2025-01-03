package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.service.TaskSkillService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/task-skills")
@Tag(name = "Task Skills", description = "APIs for task skills management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class TaskSkillController {
	
    private final TaskSkillService taskSkillService;

    @GetMapping("{taskId}")
    public List<Skill> getTaskSkills(@PathVariable("taskId") UUID taskId, Principal principal) {
    	return taskSkillService.getTaskSkills(taskId, principal.getName());
    }
    
    @PostMapping("{taskId}")
    public void addTaskSkills(@PathVariable("taskId") UUID taskId, 
    		@RequestBody List<String> skillIdList, Principal principal) {
    	taskSkillService.addTaskSkills(taskId, skillIdList, principal.getName());
    }
    
    @PutMapping("{taskId}")
    public void updateTaskSkills(@PathVariable("taskId") UUID taskId, 
    		@RequestBody List<String> skillIdList, Principal principal) {
    	taskSkillService.updateTaskSkills(taskId, skillIdList, principal.getName());
    }
}
