package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ReceiptBillItem;
import wms.entity.SaleOrder;
import wms.entity.SaleOrderItem;

import java.util.List;

public interface SaleOrderItemRepo extends JpaRepository<SaleOrderItem, Long> {
    @Query(value = "select * from where order_code = :orderCode", nativeQuery = true)
    List<SaleOrderItem> search(String orderCode);
}
