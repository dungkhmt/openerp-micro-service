package com.hust.openerp.taskmanagement.controller;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.OrganizationInvitationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationInvitationForm;
import com.hust.openerp.taskmanagement.service.OrganizationInvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@Tag(name = "Organization Invitation", description = "APIs for managing organization invitations")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class OrganizationInvitationController {

    private final OrganizationInvitationService organizationInvitationService;

    @PostMapping
    public void inviteUsers(@RequestBody OrganizationInvitationForm form, Principal principal) {
        organizationInvitationService.inviteUsers(principal.getName(), form);
    }

    @GetMapping("/validate")
    public OrganizationInvitationDTO validateToken(@RequestParam("token") String token,
                                                   Principal principal) {
        return organizationInvitationService.validateToken(token, principal.getName());
    }

    @GetMapping("/pending")
    public List<OrganizationInvitationDTO> getPendingInvitationsByMe(Principal principal) {
        return organizationInvitationService.getPendingInvitationsByUserId(principal.getName());
    }

    @GetMapping("/pending/org/{id}")
    public List<OrganizationInvitationDTO> getPendingInvitationsByOrgId(Principal principal,
                                                                        @PathVariable UUID id) {
        return organizationInvitationService.getPendingInvitationsByOrgId(principal.getName(), id);
    }

    @PostMapping("/accept")
    public void acceptInvitation(@RequestParam("token") String token, Principal principal) {
        organizationInvitationService.acceptInvitation(token, principal.getName());
    }

    @PostMapping("/decline")
    public void declineInvitation(@RequestParam("token") String token, Principal principal) {
        organizationInvitationService.declineInvitation(token, principal.getName());
    }
}
