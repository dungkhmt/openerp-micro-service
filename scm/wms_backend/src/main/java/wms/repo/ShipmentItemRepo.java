package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ShipmentItem;

import java.util.List;

public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {
    ShipmentItem getShipmentItemById(long id);
    ShipmentItem getShipmentItemByCode(String code);
    @Query(value = "select * from scm_shipment_item", nativeQuery = true)
    Page<ShipmentItem> search(Pageable pageable);
    @Query(value = "select * from scm_shipment_item ssi where ssi.delivery_bill_code = :deliveryBillCode and delivery_bill_item_seq_id = :billItemSeqId", nativeQuery = true)
    List<ShipmentItem> getItemOfSameBillAndProduct(String deliveryBillCode, String billItemSeqId);

    @Query(value = "select * from scm_shipment_item where delivery_bill_code = :billCode", nativeQuery = true)
    List<ShipmentItem> getShipmentItemOfABill(String billCode);

    @Query(value = "select * from scm_shipment_item where delivery_trip_code = :tripCode", nativeQuery = true)
    Page<ShipmentItem> getShipmentItemOfATrip(Pageable pageable, String tripCode);
}
