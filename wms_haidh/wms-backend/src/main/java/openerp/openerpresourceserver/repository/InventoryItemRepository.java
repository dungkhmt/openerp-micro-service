package openerp.openerpresourceserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.projection.InventoryItemProjection;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
	Optional<InventoryItem> findByProductIdAndLotIdAndBayId(UUID productId, String lotId, UUID bayId);

	@Query("SELECT i.inventoryItemId AS inventoryItemId, p.name AS productName, b.code AS bayCode, i.lotId AS lotId, i.quantityOnHandTotal AS quantityOnHandTotal,"
			+ " i.lastUpdatedStamp AS lastUpdatedStamp " + "FROM InventoryItem i "
			+ "JOIN Product p ON i.productId = p.productId " + "JOIN Bay b ON i.bayId = b.bayId "
			+ "ORDER BY i.lastUpdatedStamp DESC")
	Page<InventoryItemProjection> findAllInventoryItems(Pageable pageable);

	@Query("SELECT i.inventoryItemId AS inventoryItemId, p.name AS productName, b.code AS bayCode, i.lotId AS lotId, i.quantityOnHandTotal AS quantityOnHandTotal, i.lastUpdatedStamp AS lastUpdatedStamp "
			+ "FROM InventoryItem i " + "JOIN Product p ON i.productId = p.productId "
			+ "JOIN Bay b ON i.bayId = b.bayId " + "WHERE i.warehouseId = :warehouseId "
			+ "ORDER BY i.lastUpdatedStamp DESC")
	Page<InventoryItemProjection> findInventoryItemsByWarehouse(@Param("warehouseId") UUID warehouseId,
			Pageable pageable);

	@Query("SELECT i.inventoryItemId AS inventoryItemId, p.name AS productName, b.code AS bayCode, i.lotId AS lotId, i.quantityOnHandTotal AS quantityOnHandTotal, i.lastUpdatedStamp AS lastUpdatedStamp "
			+ "FROM InventoryItem i " + "JOIN Product p ON i.productId = p.productId "
			+ "JOIN Bay b ON i.bayId = b.bayId " + "WHERE i.bayId = :bayId " + "ORDER BY i.lastUpdatedStamp DESC")
	Page<InventoryItemProjection> findInventoryItemsByBay(@Param("bayId") UUID bayId, Pageable pageable);

}
