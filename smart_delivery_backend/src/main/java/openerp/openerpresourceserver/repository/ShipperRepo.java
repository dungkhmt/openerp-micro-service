package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ShipperRepo extends JpaRepository<Shipper, UUID> {
    @Query("SELECT s FROM Shipper s WHERE s.hubId = :hubId")
    List<Shipper> getAllByHubId(UUID hubId);
}
