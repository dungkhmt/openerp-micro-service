package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetTypeRepo extends JpaRepository<AssetType, Integer> {
    @Query(value = "SELECT * FROM asset_management_asset_type ORDER BY since DESC", nativeQuery = true)
    List<AssetType> getAllByLastUpdate();

    @Query(value = "SELECT type_id, COUNT(*) AS asset_count FROM asset_management_asset GROUP BY type_id ORDER BY asset_count DESC LIMIT 5", nativeQuery = true)
    List<Integer> getTopTypes();
}
