package openerp.openerpresourceserver.infrastructure.output.persistence.repository;


import openerp.openerpresourceserver.application.port.port.IEntityAuthorizationPort;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.EntityAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityAuthorizationRepo extends JpaRepository<EntityAuthorization, String>, IEntityAuthorizationPort {
    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
