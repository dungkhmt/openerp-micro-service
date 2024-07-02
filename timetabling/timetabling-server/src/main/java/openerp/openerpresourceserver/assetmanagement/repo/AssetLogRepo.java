package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.AssetLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetLogRepo extends JpaRepository<AssetLog, Integer> {
}
