package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.organization.OrganizationQueryRequest;
import openerp.openerpresourceserver.dto.request.organization.OrganizationRequest;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.entity.Organization;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.repo.OrganizationRepository;
import openerp.openerpresourceserver.repo.specification.OrganizationSpecification;
import openerp.openerpresourceserver.service.OrganizationService;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    final OrganizationRepository organizationRepository;
    final EmployeeRepository employeeRepository;

    @Override
    public List<Organization> getOrganizationsByProperties(final OrganizationQueryRequest dto) {
        List<Organization> result = new ArrayList<>();
        Specification<Organization> specs = Specification
                .where(OrganizationSpecification.hasStatus(dto.getStatus()))
                .and(OrganizationSpecification.hasNoParent());
        List<Organization> roots = organizationRepository.findAll(specs);
        Queue<Organization> queue = new LinkedList<>(roots);
        while (!queue.isEmpty()) {
            Organization current = queue.poll();
            if (isMatchingOrganization(current, dto)) {
                result.add(current);
                continue;
            }
            if (current.getChildren() != null) {
                queue.addAll(current.getChildren());
            }
        }
        return result;
    }

    @Override
    public Page<Organization> getOrganizationsByProperties(final OrganizationQueryRequest dto, final PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.Direction.DESC, "id");
        Specification<Organization> specs = Specification
                .where(OrganizationSpecification.hasKeyword(dto.getKeyword()))
                .and(OrganizationSpecification.hasStatus(dto.getStatus()))
                .and(OrganizationSpecification.hasType(dto.getType()));
        return organizationRepository.findAll(specs, pageable);
    }

    @Override
    public Organization getOrganizationById(final Long id) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if (organizationOptional.isEmpty()) {
            throw new NotFoundException("Organization not found");
        }
        return organizationOptional.get();
    }

    @Override
    public Organization addOrganization(final OrganizationRequest dto) throws BadRequestException {
        if (organizationRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Organization name already exists");
        }
        Organization parent = null;
        if (dto.getParentId() != null) {
            Optional<Organization> organizationOptional = organizationRepository.findById(dto.getParentId());
            if (organizationOptional.isEmpty()) {
                throw new NotFoundException("Organization not found");
            }
            parent = organizationOptional.get();
        }
        Organization organization = Organization.builder()
                .name(dto.getName())
                .leadId(dto.getLeadId())
                .status(dto.getStatus())
                .parent(parent)
                .type(dto.getType())
                .updatedBy(SecurityUtil.getUserEmail())
                .build();
        return organizationRepository.save(organization);
    }

    @Override
    public Organization updateOrganization(final OrganizationRequest dto) throws BadRequestException {
        if (organizationRepository.existsByIdNotAndName(dto.getId(), dto.getName())) {
            throw new BadRequestException("Organization name already exists");
        }
        Organization child = organizationRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));
        Organization parent = null;
        if (dto.getParentId() != null) {
            Optional<Organization> parentOptional = organizationRepository.findById(dto.getParentId());
            if (parentOptional.isEmpty()) {
                throw new NotFoundException("Organization not found");
            }
            parent = parentOptional.get();
            if (hasCircularReference(child, parent)) {
                throw new BadRequestException("An organization cannot be its own ancestor.");
            }
        }
        child.setName(dto.getName());
        child.setLeadId(dto.getLeadId());
        child.setType(dto.getType());
        if (dto.getStatus() == StatusEnum.INACTIVE.ordinal()) {
            deleteOrganizations(dto.getId());
        }
        if (dto.getStatus() == StatusEnum.ACTIVE.ordinal()) {
            setStatusForBranch(child, StatusEnum.ACTIVE.ordinal());
        }
        child.setParent(parent);
        child.setUpdatedBy(SecurityUtil.getUserEmail());
        return organizationRepository.save(child);
    }

    @Override
    public Organization deleteOrganizations(final Long id) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if (organizationOptional.isEmpty()) {
            throw new NotFoundException("Organization not found");
        }
        Organization organization = organizationOptional.get();
        if (organization.getParent() != null) {
            organization.setParent(null);

            organization.setUpdatedBy(SecurityUtil.getUserEmail());
            return organizationRepository.save(organization);
        }
        setStatusForBranch(organization, StatusEnum.INACTIVE.ordinal());
        return organizationRepository.save(organization);
    }

    private boolean hasCircularReference(Organization child, Organization parent) {
        if (parent == null) {
            return false;
        }
        if (Objects.equals(parent.getId(), child.getId())) {
            return true;
        }
        return hasCircularReference(child, parent.getParent());
    }

    private void setStatusForBranch(Organization organization, int status) {
        organization.setStatus(status);
        organization.setUpdatedBy(SecurityUtil.getUserEmail());
        organizationRepository.save(organization);
        Collection<Employee> employees = organization.getEmployees();
        if (employees != null && !employees.isEmpty()) {
            for (Employee employee : employees) {
                employee.setStatus(status);
                employee.setUpdatedBy(SecurityUtil.getUserEmail());
                employeeRepository.save(employee);
            }
        }
        Collection<Organization> children = organization.getChildren();
        if (children != null && !children.isEmpty()) {
            for (Organization child : children) {
                setStatusForBranch(child, status);
            }
        }
    }

    private boolean isMatchingOrganization(Organization organization, OrganizationQueryRequest dto) {
        boolean matchesKeyword = isKeywordMatching(organization.getName(), dto.getKeyword());
        boolean matchesStatus = Objects.equals(organization.getStatus(), dto.getStatus());
        return matchesKeyword && matchesStatus;
    }

    private boolean isKeywordMatching(String name, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        return name != null && name.toLowerCase().contains(keyword.toLowerCase());
    }
}
