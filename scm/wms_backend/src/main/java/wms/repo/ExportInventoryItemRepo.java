package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ExportInventoryItem;

public interface ExportInventoryItemRepo extends JpaRepository<ExportInventoryItem, Long> {
}
