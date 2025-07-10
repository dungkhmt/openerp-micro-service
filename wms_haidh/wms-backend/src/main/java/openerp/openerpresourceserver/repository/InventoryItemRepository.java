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

import openerp.openerpresourceserver.dto.response.BayResponse;
import openerp.openerpresourceserver.dto.response.InventoryItemResponse;
import openerp.openerpresourceserver.dto.response.LotIdResponse;
import openerp.openerpresourceserver.entity.InventoryItem;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

	Optional<InventoryItem> findByProductIdAndLotIdAndBayId(UUID productId, String lotId, UUID bayId);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.InventoryItemResponse(
			    i.inventoryItemId,
			    p.name,
			    i.lotId,
			    i.availableQuantity,
			    i.quantityOnHandTotal,
			    i.lastUpdatedStamp)
			FROM InventoryItem i
			JOIN Product p ON i.productId = p.productId
			WHERE i.bayId = :bayId
			AND i.lotId = :lotId
			AND i.quantityOnHandTotal > 0
			AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
			""")
	Page<InventoryItemResponse> findInventoryItems(@Param("bayId") UUID bayId, @Param("lotId") String lotId,
			@Param("search") String search, Pageable pageable);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.InventoryItemResponse(
			    i.inventoryItemId,
			    p.name,
			    i.lotId,
			    i.availableQuantity,
			    i.quantityOnHandTotal,
			    i.lastUpdatedStamp)
			FROM InventoryItem i
			JOIN Product p ON i.productId = p.productId
			WHERE i.bayId = :bayId
		    AND i.quantityOnHandTotal > 0
			AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
			""")
	Page<InventoryItemResponse> findAllInventoryItems(UUID bayId, String search, Pageable pageable);

	@Query("""
			    SELECT DISTINCT ii.warehouseId
			    FROM InventoryItem ii
			    WHERE ii.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    ) AND ii.quantityOnHandTotal > 0
			""")

	List<UUID> findDistinctWarehouseIdsWithStockBySaleOrderItemId(@Param("saleOrderItemId") UUID saleOrderItemId);

	@Query("""
			    SELECT DISTINCT new openerp.openerpresourceserver.dto.response.BayResponse(
			     b.bayId,
			     b.code
			 )
			    FROM InventoryItem i
			    JOIN Bay b ON i.bayId = b.bayId
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.quantityOnHandTotal > 0
			    AND b.warehouseId = :warehouseId
			""")
	List<BayResponse> findBaysBySaleOrderItemIdAndWarehouseId(@Param("saleOrderItemId") UUID saleOrderItemId,
			@Param("warehouseId") UUID warehouseId);

	@Query("""
			    SELECT DISTINCT new openerp.openerpresourceserver.dto.response.LotIdResponse(i.lotId)
			    FROM InventoryItem i
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.bayId = :bayId
			    AND i.quantityOnHandTotal > 0
			""")
	List<LotIdResponse> findLotIdsBySaleOrderItemIdAndBayId(@Param("saleOrderItemId") UUID saleOrderItemId,
			@Param("bayId") UUID bayId);

	@Query("""
			    SELECT i.availableQuantity
			    FROM InventoryItem i
			    WHERE i.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			    AND i.bayId = :bayId
			    AND i.lotId = :lotId
			""")
	Optional<Integer> findAvailableQuantityBySaleOrderItemIdBayIdAndLotId(
			@Param("saleOrderItemId") UUID saleOrderItemId, @Param("bayId") UUID bayId, @Param("lotId") String lotId);

	@Query("""
			SELECT DISTINCT i.lotId
			FROM InventoryItem i
			WHERE i.bayId = :bayId
			""")
	List<String> findDistinctLotIdsByBayId(@Param("bayId") UUID bayId);

}
