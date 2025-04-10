package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.MemberUserDto;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project-members")
@Tag(name = "Project Member", description = "APIs for project member management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class ProjectMemberController {
    private final ModelMapper modelMapper;
    private final ProjectMemberService projectMemberService;

    @GetMapping("{projectId}")
    public List<MemberUserDto> getAllMembers(Principal principal, @PathVariable("projectId") UUID projectId) {
        var entities = projectMemberService.getMembersOfProject(projectId, principal.getName());
        return entities.stream().map(entity -> modelMapper.map(entity, MemberUserDto.class)).toList();
    }
    
    @GetMapping("{projectId}/my-role")
    public String getMyRole(Principal principal, @PathVariable("projectId") UUID projectId) {
    	return projectMemberService.getMemberRole(principal.getName(), projectId);
    }

    @PostMapping
    public void addMemberToProject(Principal principal, @RequestBody ProjectMember projectMember) {
        projectMemberService.addMemberToProject(projectMember, principal.getName());
    }
    
    @DeleteMapping("/{projectId}/{memberId}/{roleId}")
    public void deleteMemberFromProject(Principal principal, @PathVariable UUID projectId, @PathVariable String memberId, @PathVariable String roleId) {
        projectMemberService.deleteMemberFromProject(principal.getName(), projectId, memberId, roleId);
    }
    
    @PutMapping
    public void updateMemberRole(Principal principal, @RequestBody ProjectMember projectMember) {
        projectMemberService.updateMemberRole(principal.getName(), projectMember);
    }
}
