package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBillItem;
import wms.entity.ReceiptBillItem;

import java.util.List;

public interface DeliveryBillItemRepo extends JpaRepository<DeliveryBillItem, Long> {
    @Query(value = "select * from scm_delivery_bill_item where order_code = :orderCode", nativeQuery = true)
    List<DeliveryBillItem> search(String orderCode);

    @Query(value = "select dbi.* from scm_delivery_bill_item dbi left join scm_delivery_bill db on dbi.delivery_bill_code = db.code\n" +
            "where db.code = :billCode", nativeQuery = true)
    List<DeliveryBillItem> getAllItemOfABill(String billCode);
    @Query(value = "select dbi.* from scm_delivery_bill_item dbi left join scm_delivery_bill db on dbi.delivery_bill_code = db.code\n" +
            "            where db.code = :billCode and dbi.item_seq_id = :seqId", nativeQuery = true)
    List<DeliveryBillItem> getItemOfABillBySeq(String billCode, String seqId);
}
