package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.organization.OrganizationQueryRequest;
import openerp.openerpresourceserver.dto.request.organization.OrganizationRequest;
import openerp.openerpresourceserver.entity.Organization;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrganizationService {
    List<Organization> getOrganizationsByProperties(OrganizationQueryRequest dto);
    Page<Organization> getOrganizationsByProperties(OrganizationQueryRequest dto, PagingRequest pagingRequest);

    Organization getOrganizationById(Long id);

    Organization addOrganization(OrganizationRequest dto) throws BadRequestException;

    Organization updateOrganization(OrganizationRequest dto) throws BadRequestException;

    Organization deleteOrganizations(Long id);
}
