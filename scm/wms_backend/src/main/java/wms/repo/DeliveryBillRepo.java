package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBill;

import java.util.List;

public interface DeliveryBillRepo extends JpaRepository<DeliveryBill, Long> {
    @Query(value = "select * from scm_delivery_bill where code = :code and order_code = :orderCode", nativeQuery = true)
    DeliveryBill getBillOfSaleOrderByCode(String orderCode, String code);

    @Query(value = "select * from scm_delivery_bill", nativeQuery = true)
    Page<DeliveryBill> search(Pageable pageable);
    @Query(value = "select * from scm_delivery_bill where order_code = :orderCode or :orderCode is null or :orderCode = ''", nativeQuery = true)
    Page<DeliveryBill> search(Pageable pageable, String orderCode);

    @Query(value = "select * from scm_delivery_bill where order_code = :orderCode", nativeQuery = true)
    List<DeliveryBill> getAllBillOfOrder(String orderCode);

    @Query(value = "select * from scm_delivery_bill where code = :billCode", nativeQuery = true)
    DeliveryBill getBillWithCode(String billCode);
}
