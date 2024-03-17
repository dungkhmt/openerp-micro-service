package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetTypeRepo extends JpaRepository<AssetType, Integer> {
}
