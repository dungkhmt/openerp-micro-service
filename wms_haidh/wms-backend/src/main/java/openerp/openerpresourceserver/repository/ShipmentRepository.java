package openerp.openerpresourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.entity.Shipment;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {

	List<Shipment> findAllByOrderByExpectedDeliveryStampDesc();
}
