package com.hust.openerp.taskmanagement.controller;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.OrganizationUserDTO;
import com.hust.openerp.taskmanagement.service.OrganizationUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations/{id}/users")
@Tag(name = "Organization User", description = "APIs for managing users in an organization")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class OrganizationUserController {

    private final OrganizationUserService organizationUserService;

    @GetMapping
    public List<OrganizationUserDTO> getUsersByOrganizationId(
        Principal principal,
        @PathVariable UUID id) {
        return organizationUserService.getUsersByOrganizationId(principal.getName(), id);
    }

    @DeleteMapping("/{userId}")
    public void removeUserFromOrganization(Principal principal,
                                           @PathVariable UUID id,
                                           @PathVariable String userId) {
        organizationUserService.removeUserFromOrganization(principal.getName(), id, userId);
    }
}
