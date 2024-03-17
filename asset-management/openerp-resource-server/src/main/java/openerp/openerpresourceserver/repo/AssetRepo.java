package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Integer> {
}
