package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepo extends JpaRepository<Vendor, Integer> {
}
