package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ManagementCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementCodeRepo extends JpaRepository<ManagementCode, Long> {
}
