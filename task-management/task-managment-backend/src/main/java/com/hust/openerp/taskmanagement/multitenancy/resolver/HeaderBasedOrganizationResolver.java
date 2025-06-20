package com.hust.openerp.taskmanagement.multitenancy.resolver;

import com.hust.openerp.taskmanagement.entity.Organization;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.Set;

import static com.hust.openerp.taskmanagement.model.DefaultTenantId.DEFAULT_TENANT_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeaderBasedOrganizationResolver implements OrganizationResolver {

    private final OrganizationRepository organizationRepository;

    private static final Set<String> SHARED_ENTITY_ENDPOINTS = new HashSet<>();
    static {
        SHARED_ENTITY_ENDPOINTS.add("/api/entity-authorization");
        SHARED_ENTITY_ENDPOINTS.add("/api/organizations");
        SHARED_ENTITY_ENDPOINTS.add("/api/users");
        SHARED_ENTITY_ENDPOINTS.add("/api/statuses");
        SHARED_ENTITY_ENDPOINTS.add("/api/task-categories");
        SHARED_ENTITY_ENDPOINTS.add("/api/task-priorities");
        SHARED_ENTITY_ENDPOINTS.add("/api/invitations/validate");
        // Add other shared entity endpoints
    }

    @Override
    public String resolveOrganizationCode() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String requestUri = attributes.getRequest().getRequestURI();
            for (String endpoint : SHARED_ENTITY_ENDPOINTS) {
                if (requestUri.startsWith(endpoint)) {
                    return DEFAULT_TENANT_ID.getId();
                }
            }
            String organizationCode = attributes.getRequest().getHeader("X-Organization-Code");
            if (organizationCode != null) {
                Organization org = organizationRepository.findByCode(organizationCode)
                    .orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_NOT_FOUND));
                return org.getCode();
            }
            log.error("Missing required header: X-Organization-Code for URI: {}", requestUri);
        }
        return DEFAULT_TENANT_ID.getId();
    }
}
