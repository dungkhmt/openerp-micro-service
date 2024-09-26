package wms.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.EntityAuthorization;

import java.util.List;

public interface EntityAuthorizationRepo extends JpaRepository<EntityAuthorization, String> {

    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
