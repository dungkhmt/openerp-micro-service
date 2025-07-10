package openerp.openerpresourceserver.service.impl;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.EntityAuthorization;
import openerp.openerpresourceserver.repository.EntityAuthorizationRepo;
import openerp.openerpresourceserver.service.EntityAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class EntityAuthorizationServiceImpl implements EntityAuthorizationService {

    private EntityAuthorizationRepo entityAuthorizationRepo;

    @Autowired
    public EntityAuthorizationServiceImpl(EntityAuthorizationRepo entityAuthorizationRepo) {
        this.entityAuthorizationRepo = entityAuthorizationRepo;
    }

    @Override
    public Set<String> getEntityAuthorization(String id, List<String> roleIds) {
        List<EntityAuthorization> entityAuthorizations = entityAuthorizationRepo.findAllByIdStartingWithAndRoleIdIn(id, roleIds);

        log.info("EntityAuthorization1: {}", id, roleIds,entityAuthorizations);
        if (entityAuthorizations != null) {
            // Log each EntityAuthorization object
            entityAuthorizations.forEach(entityAuth -> log.info("EntityAuthorization: {}", entityAuth));

            return entityAuthorizations.stream()
                    .map(EntityAuthorization::getId)
                    .collect(Collectors.toSet());
        } else {
            log.info("No EntityAuthorization found for id: {} and roleIds: {}", id, roleIds);
            return Collections.emptySet();
        }    }
}
