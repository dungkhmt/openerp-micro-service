package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.OpenBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenBatchRepo extends JpaRepository<OpenBatch, Long> {
}
