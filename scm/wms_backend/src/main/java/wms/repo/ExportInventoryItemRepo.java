package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryBillItem;
import wms.entity.ExportInventoryItem;

import java.util.List;

public interface ExportInventoryItemRepo extends JpaRepository<ExportInventoryItem, Long> {
    @Query(value = "select * from scm_export_inventory_item where delivery_bill_code = :billCode", nativeQuery = true)
    List<ExportInventoryItem> search(String billCode);
}
