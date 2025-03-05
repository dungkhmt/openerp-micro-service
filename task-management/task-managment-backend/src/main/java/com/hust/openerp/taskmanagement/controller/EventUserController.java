package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.service.EventUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/event-users")
@Tag(name = "Event User", description = "APIs for event user management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class EventUserController {
    private final EventUserService eventUserService;

    @GetMapping
    public List<User> getEventUsers(Principal principal, 
    		@RequestParam("eventId") UUID eventId) {
        return eventUserService.getEventUsers(principal.getName(), eventId);
    }

}
