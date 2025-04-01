package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.UserSkillDTO;
import com.hust.openerp.taskmanagement.service.UserSkillService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-skills")
@Tag(name = "User Skills", description = "APIs for user skills management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class UserSkillController {
    private final UserSkillService userSkillService;

    @GetMapping("/me")
    public List<UserSkillDTO> getMySkills(Principal principal) {
        return userSkillService.getMySkills(principal.getName());
    }

    @PutMapping("/me")
    public void updateMySkills(Principal principal, 
    		@RequestBody List<String> skillList) {
        userSkillService.updateMySkills(principal.getName(), skillList);
    }

}
