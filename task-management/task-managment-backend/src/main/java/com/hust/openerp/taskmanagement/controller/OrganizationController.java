package com.hust.openerp.taskmanagement.controller;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.OrganizationDTO;
import com.hust.openerp.taskmanagement.dto.OrganizationInvitationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationForm;
import com.hust.openerp.taskmanagement.service.OrganizationInvitationService;
import com.hust.openerp.taskmanagement.service.OrganizationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
@Tag(name = "Organization", description = "APIs for organization management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationInvitationService organizationInvitationService;

    @GetMapping
    public List<OrganizationDTO> getOrganizationsByMe(Principal principal) {
        return organizationService.getOrganizationsByUserId(principal.getName());
    }

    @GetMapping("{id}")
    public OrganizationDTO getOrganizationById(Principal principal, @PathVariable UUID id) {
        return organizationService.getOrganizationById(principal.getName(), id);
    }

    @GetMapping("/last")
    public OrganizationDTO getLastOrganizationByMe(Principal principal) {
        return organizationService.getLastOrganizationByUserId(principal.getName());
    }

    @PostMapping
    public OrganizationDTO createOrganization(Principal principal, @Valid @RequestBody OrganizationForm request) {
        return organizationService.createOrganization(principal.getName(), request);
    }

    @PutMapping("/{id}")
    public OrganizationDTO updateOrganization(Principal principal,
                                              @PathVariable UUID id,
                                              @Valid @RequestBody OrganizationForm request) {
        return organizationService.updateOrganization(principal.getName(), id, request);
    }
}
