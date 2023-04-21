package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;

public interface PurchaseOrderRepo  extends JpaRepository<PurchaseOrder, Long>  {
    @Query(value = "select * from purchase_order where status != 'DELETED' ", nativeQuery = true)
    Page<PurchaseOrder> search(Pageable pageable);
    PurchaseOrder getOrderById(long id);
    PurchaseOrder getOrderByCode(String code);
}
