package openerp.openerpresourceserver.repository;


import openerp.openerpresourceserver.entity.EntityAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntityAuthorizationRepository extends JpaRepository<EntityAuthorization, String> {

    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
