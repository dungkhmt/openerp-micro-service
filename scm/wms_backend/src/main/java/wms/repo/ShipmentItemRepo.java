package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ShipmentItem;

public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {
    ShipmentItem getShipmentItemById(long id);
    ShipmentItem getShipmentItemByCode(String code);
    @Query(value = "select * from shipment_item", nativeQuery = true)
    Page<ShipmentItem> search(Pageable pageable);
}
