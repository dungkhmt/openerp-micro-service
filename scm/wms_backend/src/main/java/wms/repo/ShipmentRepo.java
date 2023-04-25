package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.Shipment;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    Shipment getShipmentById(long id);
    Shipment getShipmentByCode(String code);
}
