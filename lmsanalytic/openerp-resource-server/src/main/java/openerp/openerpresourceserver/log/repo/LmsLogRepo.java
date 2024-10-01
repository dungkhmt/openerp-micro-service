package openerp.openerpresourceserver.log.repo;

import openerp.openerpresourceserver.log.entity.LmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LmsLogRepo extends JpaRepository<LmsLog, UUID> {

}
