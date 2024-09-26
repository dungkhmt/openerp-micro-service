package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ReceiptBill;

import java.util.List;

public interface ReceiptBillRepo extends JpaRepository<ReceiptBill, Long> {
    @Query(value = "select * from scm_receipt_bill where code = :code and order_code = :orderCode", nativeQuery = true)
    ReceiptBill getBillOfOrderByCode(String orderCode, String code);

    @Query(value = "select * from scm_receipt_bill where order_code = :orderCode or order_code = ''", nativeQuery = true)
    Page<ReceiptBill> getAllBills(Pageable pageable, String orderCode);

    @Query(value = "select * from scm_receipt_bill where order_code = :orderCode", nativeQuery = true)
    List<ReceiptBill> getAllBillOfOrder(String orderCode);
}
