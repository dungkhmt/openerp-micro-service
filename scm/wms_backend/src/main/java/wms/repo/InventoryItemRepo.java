package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.InventoryItem;

import java.util.List;

public interface InventoryItemRepo extends JpaRepository<InventoryItem, Long> {
    @Query(value = "select * from scm_inventory_item where product_code = :productCode order by Date(scm_inventory_item.expire_date) asc", nativeQuery = true)
    List<InventoryItem> getAllItemsOfSameProduct(String productCode);
}