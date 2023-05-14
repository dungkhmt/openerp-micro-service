package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    List<InventoryItem> getInventoryItemByProductIdAndBayIdAndWarehouseIdOrderByCreatedStamp(UUID productId, UUID bayId, UUID warehouseId);

    List<InventoryItem> findAllByWarehouseId(UUID warehouseId);

}
