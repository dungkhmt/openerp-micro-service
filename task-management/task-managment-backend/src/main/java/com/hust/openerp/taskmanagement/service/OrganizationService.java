package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.OrganizationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationForm;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<OrganizationDTO> getOrganizationsByUserId(String userId);

    OrganizationDTO getOrganizationById(String currentUserId, UUID id);

    OrganizationDTO getLastOrganizationByUserId(String userId);

    OrganizationDTO createOrganization(String userId, OrganizationForm request);

    OrganizationDTO updateOrganization(String currentUserId, UUID id, OrganizationForm request);
}
