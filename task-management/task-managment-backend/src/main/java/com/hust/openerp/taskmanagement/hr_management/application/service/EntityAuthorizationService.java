package com.hust.openerp.taskmanagement.hr_management.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.application.port.in.port.IEntityAuthorizationPort;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.EntityAuthorization;
import openerp.openerpresourceserver.application.port.out.EntityAuthorizationUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EntityAuthorizationService implements EntityAuthorizationUseCase {
    private IEntityAuthorizationPort entityAuthorizationPort;

    @Override
    public Set<String> getEntityAuthorization(String id, List<String> roleIds) {
        List<EntityAuthorization> entityAuthorizations = entityAuthorizationPort.findAllByIdStartingWithAndRoleIdIn(id, roleIds);

        return entityAuthorizations != null ? entityAuthorizations.stream().map(EntityAuthorization::getId).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
