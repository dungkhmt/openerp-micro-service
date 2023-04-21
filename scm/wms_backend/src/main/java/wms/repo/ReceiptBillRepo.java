package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ReceiptBill;

public interface ReceiptBillRepo extends JpaRepository<ReceiptBill, Long> {
    ReceiptBill getReceiptBillByCode(String code);
}
