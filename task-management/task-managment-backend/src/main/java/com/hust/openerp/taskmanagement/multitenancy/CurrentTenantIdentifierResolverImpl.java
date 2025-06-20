package com.hust.openerp.taskmanagement.multitenancy;

import com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.hust.openerp.taskmanagement.model.DefaultTenantId.DEFAULT_TENANT_ID;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String organizationCode = OrganizationContext.getCurrentOrganizationCode();
        return organizationCode != null ? organizationCode : DEFAULT_TENANT_ID.getId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}