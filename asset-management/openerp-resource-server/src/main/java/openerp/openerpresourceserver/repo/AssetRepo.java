package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Integer> {
    @Query(value = "SELECT * FROM asset_management_asset WHERE status_id = :status_id", nativeQuery = true)
    List<Asset> findByStatusId(Integer status_id);

    @Query(value = "SELECT * FROM asset_management_asset WHERE assignee_id = :user_id", nativeQuery = true)
    List<Asset> findByAssigneeId(String user_id);
}
