package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AssetLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetLogRepo extends JpaRepository<AssetLog, Integer> {
}
