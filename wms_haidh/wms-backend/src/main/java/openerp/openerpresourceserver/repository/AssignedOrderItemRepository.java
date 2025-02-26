package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.projection.AssignedOrderItemProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryOrderItemProjection;

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
			        o.orderId AS orderId,
			        o.orderDate AS orderDate,
			        p.name AS productName,
			        p.weight AS weight,
			        aoi.originalQuantity AS originalQuantity,
			        b.code AS bayCode,
			        aoi.lotId AS lotId
			    FROM AssignedOrderItem aoi
			    JOIN Order o ON aoi.orderId = o.orderId
			    JOIN Product p ON aoi.productId = p.productId
			    JOIN Bay b ON aoi.bayId = b.bayId
			    WHERE aoi.warehouseId = :warehouseId
			    AND aoi.status = 'CREATED'
			""")
	Page<DeliveryOrderItemProjection> findAllDeliveryOrderItemsByWarehouse(@Param("warehouseId") UUID warehouseId, Pageable pageable);
}
