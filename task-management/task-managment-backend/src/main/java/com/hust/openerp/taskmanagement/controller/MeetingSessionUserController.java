package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionUserDTO;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberRegistrationsForm;
import com.hust.openerp.taskmanagement.service.MeetingSessionUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meeting-plans/{planId}/session-user")
@Tag(name = "Meeting Session User", description = "APIs for managing user assignments in meeting sessions")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class MeetingSessionUserController {

	private final MeetingSessionUserService meetingSessionUserService;

	@GetMapping("/me")
	public List<MeetingSessionDTO> getSessionsByMe(@PathVariable UUID planId, Principal principal) {
		return meetingSessionUserService.getSessionsByMe(planId, principal.getName());
	}

	@GetMapping
	public List<MeetingSessionUserDTO> getAllSessionRegistrations(@PathVariable UUID planId, Principal principal) {
		return meetingSessionUserService.getAllSessionRegistrations(planId, principal.getName());
	}

	@PutMapping
	public void updateMyMeetingSessions(@PathVariable UUID planId, Principal principal,
			@RequestBody UpdateMemberRegistrationsForm form) {
		meetingSessionUserService.updateMyMeetingSessions(planId, principal.getName(), form);
	}
}
