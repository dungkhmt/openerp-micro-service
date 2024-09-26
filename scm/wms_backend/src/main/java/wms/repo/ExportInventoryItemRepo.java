package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBillItem;
import wms.entity.ExportInventoryItem;
import wms.entity.InventoryItem;

import java.util.List;

public interface ExportInventoryItemRepo extends JpaRepository<ExportInventoryItem, Long> {
    @Query(value = "select * from scm_export_inventory_item where delivery_bill_code = :billCode", nativeQuery = true)
    List<ExportInventoryItem> search(String billCode);

    @Query(value = "select * from scm_export_inventory_item where delivery_bill_item_seq_id = :billItemSeqId", nativeQuery = true)
    List<ExportInventoryItem> getAllItemsOfSameProduct(String billItemSeqId);
}
