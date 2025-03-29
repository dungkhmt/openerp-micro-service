package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.BatchCreateSessionForm;
import com.hust.openerp.taskmanagement.service.MeetingSessionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meeting-plans/{planId}/sessions")
@Tag(name = "Meeting Session", description = "APIs for managing meeting sessions")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class MeetingSessionController {
    private final MeetingSessionService meetingSessionService;

    @GetMapping
    public List<MeetingSessionDTO> getSessionsByPlanId(@PathVariable UUID planId, Principal principal) {
        return meetingSessionService.getSessionsByPlanId(planId, principal.getName());
    }

    @PostMapping
    public void createSessions(@PathVariable UUID planId, Principal principal,
                                           @RequestBody @Valid BatchCreateSessionForm createForm) {
        meetingSessionService.createSessions(planId, principal.getName(), createForm);
    }

    @DeleteMapping("/{sessionId}")
    public void deleteSession(@PathVariable UUID planId, @PathVariable UUID sessionId, Principal principal) {
        meetingSessionService.deleteSession(planId, sessionId, principal.getName());
    }
}
