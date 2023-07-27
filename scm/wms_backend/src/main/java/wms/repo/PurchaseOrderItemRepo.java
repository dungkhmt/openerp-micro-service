package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wms.entity.PurchaseOrder;
import wms.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderItemRepo extends JpaRepository<PurchaseOrderItem, Long> {
    @Query(value = "select * from scm_purchase_order_item where order_code = :orderCode and product_code = :productCode", nativeQuery = true)
    PurchaseOrderItem getItemByProductCode(String orderCode, String productCode);
    @Query(value = "select * from scm_purchase_order_item poi left join scm_product p on p.code = poi.product_code\n" +
            " where (order_code = :orderCode or :orderCode = '') and poi.is_deleted = 0", nativeQuery = true)
        Page<PurchaseOrderItem> search(Pageable pageable, String orderCode);

    @Modifying
    @Query(value = "update scm_purchase_order_item set is_deleted = 1 where id in :ids", nativeQuery = true)
    int deleteAllByIds(@Param("ids") List<Long> ids);
}
