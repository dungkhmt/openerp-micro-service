package openerp.openerpresourceserver.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.EntityAuthorization;
import openerp.openerpresourceserver.repository.EntityAuthorizationRepository;
import openerp.openerpresourceserver.service.EntityAuthorizationService;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EntityAuthorizationServiceImpl implements EntityAuthorizationService {

    private EntityAuthorizationRepository entityAuthorizationRepo;

    @Override
    public Set<String> getEntityAuthorization(String id, List<String> roleIds) {
        List<EntityAuthorization> entityAuthorizations = entityAuthorizationRepo.findAllByIdStartingWithAndRoleIdIn(id, roleIds);

        return entityAuthorizations != null ? entityAuthorizations.stream().map(EntityAuthorization::getId).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
