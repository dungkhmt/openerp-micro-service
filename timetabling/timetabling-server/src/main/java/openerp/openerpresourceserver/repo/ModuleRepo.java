package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepo extends JpaRepository<Module, Long> {
}
