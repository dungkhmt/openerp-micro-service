package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.PurchaseOrder;
import wms.entity.ReceiptBill;

public interface ReceiptBillRepo extends JpaRepository<ReceiptBill, Long> {
    @Query(value = "select * from receipt_bill where code = :code and order_code = :orderCode", nativeQuery = true)
    ReceiptBill getReceiptBillByCode(String orderCode, String code);

    @Query(value = "select * from receipt_bill", nativeQuery = true)
    Page<ReceiptBill> search(Pageable pageable);
}
