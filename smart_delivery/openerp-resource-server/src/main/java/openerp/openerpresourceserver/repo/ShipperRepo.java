package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipperRepo extends JpaRepository<Shipper, UUID> {
}
