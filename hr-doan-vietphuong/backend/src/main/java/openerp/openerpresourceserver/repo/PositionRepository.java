package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Position;
import openerp.openerpresourceserver.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {
    boolean existsByName(String name);
    boolean existsByIdNotAndName(Long id, String name);

    List<Position> findByRolesContaining(Role role);
}