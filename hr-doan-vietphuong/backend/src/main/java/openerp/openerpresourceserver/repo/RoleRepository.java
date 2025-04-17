package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Role findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByIdNotAndNameIgnoreCase(Long id, String name);

    List<Role> findAllAndByStatus(int status);
}
