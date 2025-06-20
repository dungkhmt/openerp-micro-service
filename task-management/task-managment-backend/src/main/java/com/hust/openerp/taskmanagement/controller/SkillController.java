package com.hust.openerp.taskmanagement.controller;

import java.util.List;
import java.util.UUID;

import com.hust.openerp.taskmanagement.dto.SkillDTO;
import com.hust.openerp.taskmanagement.dto.form.SkillForm;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.service.SkillService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/skills")
@Tag(name = "Skill", description = "APIs for skill management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public List<SkillDTO> getAllSkills() {
        return skillService.getAllSkills();
    }

    @PostMapping
    public SkillDTO createSkill(@RequestBody SkillForm skill) {
        return skillService.create(skill);
    }

    @DeleteMapping("{id}")
    public void deleteSkill(@PathVariable("id") UUID id) {
        skillService.delete(id);
    }
     
    @PutMapping("{id}")
    public void updateSkill(@PathVariable("id") UUID id, @RequestBody Skill updatedSkill) {
        skillService.update(id, updatedSkill);
    }
}
