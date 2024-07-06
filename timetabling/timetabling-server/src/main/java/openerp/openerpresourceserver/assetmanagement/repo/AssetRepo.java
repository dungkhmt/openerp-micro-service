package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Integer> {
    @Query(value = "SELECT * FROM asset_management_asset ORDER BY since DESC", nativeQuery = true)
    List<Asset> getAllByLastUpdate();

    @Query(value = "SELECT * FROM asset_management_asset WHERE status_id = :status_id", nativeQuery = true)
    List<Asset> findByStatusId(Integer status_id);

    @Query(value = "SELECT * FROM asset_management_asset WHERE assignee_id = :user_id", nativeQuery = true)
    List<Asset> findByAssigneeId(String user_id);

    @Query(value = "SELECT * FROM asset_management_asset WHERE admin_id = :adminId", nativeQuery = true)
    List<Asset> findByAdminId(String adminId);

    @Query(value = "SELECT user_id, COUNT(*) AS asset_count FROM asset_management_asset GROUP BY user_id ORDER BY asset_count DESC LIMIT 5", nativeQuery = true)
    List<String> getTopAssignUsers();

    @Query(value = "SELECT admin_id, COUNT(*) AS asset_count FROM asset_management_asset GROUP BY admin_id ORDER BY asset_count DESC LIMIT 5", nativeQuery = true)
    List<String> getTopAdminUsers();

    @Query(value = "SELECT * FROM asset_management_asset WHERE type_id = :typeId", nativeQuery = true)
    List<Asset> findByTypes(Integer typeId);

    @Query(value = "SELECT * FROM asset_management_asset WHERE assignee_id = :user_id", nativeQuery = true)
    List<Asset> assignToMe(String user_id);

    @Query(value = "SELECT * FROM asset_management_asset WHERE admin_id = :user_id", nativeQuery = true)
    List<Asset> manageByMe(String user_id);
}