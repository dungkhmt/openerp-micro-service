package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.Shipment;
import wms.entity.ShipmentItem;

public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {
    ShipmentItem getShipmentItemById(long id);
    ShipmentItem getShipmentItemByCode(String code);
}
