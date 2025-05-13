package openerp.openerpresourceserver.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.request.Item;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.projection.AssignedOrderItemProjection;
import openerp.openerpresourceserver.projection.DeliveryOrderItemProjection;

@Repository
public interface AssignedOrderItemRepository extends JpaRepository<AssignedOrderItem, UUID> {

	@Query("""
			    SELECT w.name AS warehouseName,
			           b.code AS bayCode,
			           aoi.lotId AS lotId,
			           aoi.quantity AS quantity,
			           aoi.status AS status
			    FROM AssignedOrderItem aoi
			    JOIN Bay b ON aoi.bayId = b.bayId
			    JOIN Warehouse w ON aoi.warehouseId = w.warehouseId
			    WHERE aoi.orderId = (
			        SELECT soi.orderId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    ) AND aoi.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			""")
	List<AssignedOrderItemProjection> findAssignedOrderItemsBySaleOrderItemId(
			@Param("saleOrderItemId") UUID saleOrderItemId);

	@Query("""
			    SELECT
			        aoi.assignedOrderItemId AS assignedOrderItemId,
			        aoi.orderId AS orderId,
			        p.name AS productName,
			        p.weight AS weight,
			        aoi.originalQuantity AS originalQuantity,
			        b.code AS bayCode,
			        aoi.lotId AS lotId
			    FROM AssignedOrderItem aoi
			    JOIN Product p ON aoi.productId = p.productId
			    JOIN Bay b ON aoi.bayId = b.bayId
			    JOIN Order o ON aoi.orderId = o.orderId
			    WHERE aoi.warehouseId = :warehouseId
			    AND aoi.status = 'CREATED'
			    AND o.status = 'ASSIGNED'
			""")
	Page<DeliveryOrderItemProjection> findAllDeliveryOrderItemsByWarehouse(@Param("warehouseId") UUID warehouseId,
			Pageable pageable);

	@Modifying
	@Query("UPDATE AssignedOrderItem a SET a.status = :status, a.lastUpdatedStamp = :lastUpdatedStamp WHERE a.assignedOrderItemId IN :ids")
	int updateStatusByIds(@Param("ids") List<UUID> ids, @Param("status") String status,
			@Param("lastUpdatedStamp") LocalDateTime lastUpdatedStamp);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.request.Item(a.assignedOrderItemId, a.warehouseId, o.customerAddressId, a.quantity * p.weight)
			FROM AssignedOrderItem a
			JOIN Order o ON a.orderId = o.orderId
			JOIN Product p ON a.productId = p.productId
			WHERE a.status = 'CREATED' AND o.status = 'ASSIGNED'
			""")
	List<Item> getAllItems();

	@Query("""
			    SELECT
			        a.assignedOrderItemId AS assignedOrderItemId,
			        a.orderId AS orderId,
			        p.name AS productName,
			        p.weight AS weight,
			        a.originalQuantity AS originalQuantity,
			        b.code AS bayCode,
			        a.lotId AS lotId
			    FROM AssignedOrderItem a
			    JOIN Product p ON a.productId = p.productId
			    JOIN Bay b ON a.bayId = b.bayId
			    WHERE a.assignedOrderItemId IN :assignedOrderItemIds
			""")
	List<DeliveryOrderItemProjection> findDeliveryOrderItemsByIds(List<UUID> assignedOrderItemIds);

	@Query("SELECT COUNT(a) FROM AssignedOrderItem a WHERE a.orderId = :orderId")
    long countAssignedItems(@Param("orderId") UUID orderId);

}
