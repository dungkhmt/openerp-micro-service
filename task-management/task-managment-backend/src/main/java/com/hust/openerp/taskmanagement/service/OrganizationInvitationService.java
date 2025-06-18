package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.OrganizationInvitationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationInvitationForm;
import com.hust.openerp.taskmanagement.entity.OrganizationInvitation;

import java.util.List;
import java.util.UUID;

public interface OrganizationInvitationService {
    void inviteUsers(String currentUserId, OrganizationInvitationForm form);

    List<OrganizationInvitationDTO> getPendingInvitationsByUserId(String userId);

    OrganizationInvitationDTO validateToken(String token, String currentUserId);

    void acceptInvitation(String token, String currentUserId);

    void declineInvitation(String token, String currentUserId);

    List<OrganizationInvitationDTO> getPendingInvitationsByOrgId(String currentUserId, UUID id);
}

