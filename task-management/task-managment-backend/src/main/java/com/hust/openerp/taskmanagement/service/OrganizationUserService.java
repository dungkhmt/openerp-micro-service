package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.OrganizationUserDTO;

import java.util.List;
import java.util.UUID;

public interface OrganizationUserService {
    List<OrganizationUserDTO> getUsersByOrganizationId(String name, UUID id);

    void removeUserFromOrganization(String currentUserId, UUID organizationId, String userId);
}
