package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VendorRepo extends JpaRepository<Vendor, Integer> {
    @Query(value = "SELECT * FROM asset_management_vendor ORDER BY since DESC", nativeQuery = true)
    List<Vendor> getAllByLastUpdate();
}

