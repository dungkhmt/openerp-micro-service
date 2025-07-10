package openerp.openerpresourceserver.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {

	@Query("SELECT s FROM Shipment s WHERE s.expectedDeliveryStamp >= CURRENT_TIMESTAMP ORDER BY s.expectedDeliveryStamp DESC")
    List<Shipment> findAllUpcomingShipments();

	Page<Shipment> findByShipmentIdContainingIgnoreCase(String shipmentId, Pageable pageable);

}
