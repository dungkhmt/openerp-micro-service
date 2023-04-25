package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBill;
import wms.entity.ReceiptBill;

import java.util.List;

public interface DeliveryBillRepo extends JpaRepository<DeliveryBill, Long> {
    @Query(value = "select * from delivery_bill where code = :code and order_code = :orderCode", nativeQuery = true)
    DeliveryBill getDeliveryBillByCode(String orderCode, String code);

    @Query(value = "select * from delivery_bill", nativeQuery = true)
    Page<DeliveryBill> search(Pageable pageable);

    @Query(value = "select * from delivery_bill where order_code = :orderCode", nativeQuery = true)
    List<DeliveryBill> getAllBillOfOrder(String orderCode);
}
