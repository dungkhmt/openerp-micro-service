package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepo extends JpaRepository<Location, Integer> {
    @Query(value = "SELECT * FROM asset_management_location ORDER BY since DESC", nativeQuery = true)
    List<Location> getAllByLastUpdate();
}
