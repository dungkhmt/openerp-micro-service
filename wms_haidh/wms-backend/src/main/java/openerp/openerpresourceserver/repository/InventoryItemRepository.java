package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.projection.BayProjection;
import openerp.openerpresourceserver.entity.projection.InventoryItemProjection;
import openerp.openerpresourceserver.entity.projection.LotIdProjection;

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

	@Query("""
			    SELECT DISTINCT ii.warehouseId
			    FROM InventoryItem ii
			    WHERE ii.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    ) AND ii.quantityOnHandTotal > 0
			""")
	List<UUID> findDistinctWarehouseIdsWithStockBySaleOrderItemId(@Param("saleOrderItemId") UUID saleOrderItemId);

	@Query("""
			    SELECT DISTINCT b.bayId AS bayId, b.code AS code
			    FROM InventoryItem i
			    JOIN Bay b ON i.bayId = b.bayId
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.quantityOnHandTotal > 0
			    AND b.warehouseId = :warehouseId
			""")
	List<BayProjection> findBayProjectionsBySaleOrderItemIdAndWarehouseId(
			@Param("saleOrderItemId") UUID saleOrderItemId, @Param("warehouseId") UUID warehouseId);

	@Query("""
			    SELECT DISTINCT i.lotId AS lotId
			    FROM InventoryItem i
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.bayId = :bayId
			    AND i.quantityOnHandTotal > 0
			""")
	List<LotIdProjection> findLotIdsBySaleOrderItemIdAndBayId(@Param("saleOrderItemId") UUID saleOrderItemId,
			@Param("bayId") UUID bayId);

	@Query("""
			    SELECT i.quantityOnHandTotal
			    FROM InventoryItem i
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.bayId = :bayId
			    AND i.lotId = :lotId
			    AND i.quantityOnHandTotal > 0
			""")
	Optional<Integer> findQuantityOnHandBySaleOrderItemIdBayIdAndLotId(@Param("saleOrderItemId") UUID saleOrderItemId,
			@Param("bayId") UUID bayId, @Param("lotId") String lotId);
	

}
