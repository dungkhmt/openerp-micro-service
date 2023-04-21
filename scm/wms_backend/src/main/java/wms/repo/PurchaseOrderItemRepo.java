package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;

public interface PurchaseOrderItemRepo extends JpaRepository<PurchaseOrderItem, Long> {
    @Query(value = "select * from purchase_order_item where order_code = :orderCode and product_code = :productCode", nativeQuery = true)
    PurchaseOrderItem getItemByProductCode(String orderCode, String productCode);
}
