package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBillItem;
import wms.entity.ReceiptBillItem;

import java.util.List;

public interface DeliveryBillItemRepo extends JpaRepository<DeliveryBillItem, Long> {
    @Query(value = "select * from delivery_bill_item where order_code = :orderCode", nativeQuery = true)
    List<DeliveryBillItem> search(String orderCode);
}
