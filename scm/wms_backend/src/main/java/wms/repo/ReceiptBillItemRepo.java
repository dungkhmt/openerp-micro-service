package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ReceiptBill;
import wms.entity.ReceiptBillItem;

import java.util.List;

public interface ReceiptBillItemRepo extends JpaRepository<ReceiptBillItem, Long> {
    @Query(value = "select * from receipt_bill_item where order_code = :orderCode", nativeQuery = true)
    List<ReceiptBillItem> search(String orderCode);
}
