package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.dto.shipment.ReturnShipmentItemDTO;
import wms.dto.shipment.ShipmentProjection;
import wms.entity.Facility;
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

//    @Query(value = "select * from scm_shipment_item where delivery_trip_code = :tripCode", nativeQuery = true)
    @Query(value = "select ssi.*, sdbi.product_code, sp.name from scm_shipment_item ssi left join scm_delivery_bill_item sdbi on ssi.delivery_bill_code = sdbi.delivery_bill_code and ssi.delivery_bill_item_seq_id = sdbi.item_seq_id left join scm_product sp on sdbi.product_code = sp.code where ssi.delivery_trip_code = :tripCode", nativeQuery = true)
    Page<ShipmentItem> getShipmentItemOfATrip(Pageable pageable, String tripCode);

    @Query(value = "select * from scm_shipment_item where delivery_trip_code = :tripCode", nativeQuery = true)
    List<ShipmentItem> getShipmentItemOfATrip(String tripCode);

    @Query(value = "SELECT *\n" +
            "FROM scm_shipment_item\n" +
            "where EXTRACT(YEAR FROM created_date) = :year and\n" +
            "       EXTRACT(MONTH FROM created_date) = :month", nativeQuery = true)
    List<ShipmentItem> getShipmentItemsOfMonth(int month, int year);
}
