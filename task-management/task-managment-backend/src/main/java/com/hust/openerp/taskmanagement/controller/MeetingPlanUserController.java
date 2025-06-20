package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignRequestDTO;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.AddMeetingPlanUserForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberAssignmentsForm;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.service.MeetingPlanUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meeting-plans/{planId}/users")
@Tag(name = "Meeting Plan User", description = "APIs for managing users in a meeting plan")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class MeetingPlanUserController {

    private final MeetingPlanUserService meetingPlanUserService;

    @GetMapping
    public List<User> getAllUsersInMeetingPlan(Principal principal, @PathVariable UUID planId) {
        return meetingPlanUserService.getAllMeetingPlanUsers(principal.getName(), planId);
    }

    @PostMapping
    public void addUserToMeetingPlan(Principal principal, @PathVariable UUID planId,
                                                   @RequestBody @Valid AddMeetingPlanUserForm addForm) {
        addForm.setPlanId(planId);
        meetingPlanUserService.addMeetingPlanUser(principal.getName(), addForm);
    }

    @DeleteMapping("/{userId}")
    public void removeUserFromMeetingPlan(Principal principal, @PathVariable UUID planId, @PathVariable String userId) {
        meetingPlanUserService.removeMeetingPlanUser(principal.getName(), planId, userId);
    }
    
    @GetMapping("/assignments/me")
    public MeetingSessionDTO getMyAssignment(Principal principal, @PathVariable UUID planId) {
        return meetingPlanUserService.getMyAssignment(principal.getName(), planId);
    }
    
    @GetMapping("/assignments")
    public List<MeetingPlanUserDTO> getMemberAssignments(Principal principal, @PathVariable UUID planId) {
        return meetingPlanUserService.getMemberAssignments(principal.getName(), planId);
    }
    
    @PutMapping("/assignments")
    public void updateMemberAssignments(Principal principal, @PathVariable UUID planId, 
    		@RequestBody @Valid UpdateMemberAssignmentsForm assignments) {
        meetingPlanUserService.updateMemberAssignment(principal.getName(), planId, assignments);
    }
    
    @PostMapping("/auto-assign")
    public MeetingAutoAssignResponseDTO autoAssignMembers(Principal principal, @PathVariable UUID planId,
    		@RequestBody @Valid MeetingAutoAssignRequestDTO request) {
        
    	return meetingPlanUserService.autoAssignMembers(principal.getName(), planId, request);
    }
}
