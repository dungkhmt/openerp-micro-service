package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;

public interface PurchaseOrderItemRepo extends JpaRepository<PurchaseOrderItem, Long> {
    @Query(value = "select * from scm_purchase_order_item where order_code = :orderCode and product_code = :productCode", nativeQuery = true)
    PurchaseOrderItem getItemByProductCode(String orderCode, String productCode);
    @Query(value = "select * from scm_purchase_order_item poi left join scm_product p on p.code = poi.product_code\n" +
            " where order_code = :orderCode or :orderCode = ''", nativeQuery = true)
    Page<PurchaseOrderItem> search(Pageable pageable, String orderCode);
}
