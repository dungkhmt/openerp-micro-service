package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ReceiptBillItem;

public interface ReceiptBillItemRepo extends JpaRepository<ReceiptBillItem, Long> {

}
