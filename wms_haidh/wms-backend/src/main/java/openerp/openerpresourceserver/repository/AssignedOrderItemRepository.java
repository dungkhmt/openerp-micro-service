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
import openerp.openerpresourceserver.dto.response.AssignedOrderItemResponse;
import openerp.openerpresourceserver.dto.response.DeliveryOrderItemResponse;
import openerp.openerpresourceserver.dto.response.PickedOrderItemResponse;
import openerp.openerpresourceserver.entity.AssignedOrderItem;

@Repository
public interface AssignedOrderItemRepository extends JpaRepository<AssignedOrderItem, UUID> {

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.AssignedOrderItemResponse(w.name,
			           b.code,
			           aoi.lotId,
			           aoi.quantity,
			           aoi.status AS status)
			    FROM AssignedOrderItem aoi
			    JOIN Bay b ON aoi.bayId = b.bayId
			    JOIN Warehouse w ON aoi.warehouseId = w.warehouseId
			    WHERE aoi.orderId = (
			        SELECT soi.orderId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    ) AND aoi.productId = (
			        SELECT soi.productId FROM SaleOrderItem soi WHERE soi.saleOrderItemId = :saleOrderItemId
			    )
			""")
	List<AssignedOrderItemResponse> findAssignedOrderItemsBySaleOrderItemId(
			@Param("saleOrderItemId") UUID saleOrderItemId);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.DeliveryOrderItemResponse(
			        aoi.assignedOrderItemId,
			        aoi.orderId,
			        p.name,
			        p.weight,
			        aoi.quantity,
			        b.code,
			        aoi.lotId)
			    FROM AssignedOrderItem aoi
			    JOIN Product p ON aoi.productId = p.productId
			    JOIN Bay b ON aoi.bayId = b.bayId
			    JOIN Order o ON aoi.orderId = o.orderId
			    WHERE aoi.warehouseId = :warehouseId
			    AND aoi.status='PICKED' AND (o.status = 'PICK_COMPLETE' OR o.status='DELIVERING')
			""")
	Page<DeliveryOrderItemResponse> findAllDeliveryOrderItemsByWarehouse(@Param("warehouseId") UUID warehouseId,
			Pageable pageable);

	@Query("""
			     SELECT new openerp.openerpresourceserver.dto.response.PickedOrderItemResponse(
			        aoi.assignedOrderItemId,
			        p.name,
			        aoi.quantity,
			        b.code,
			        aoi.lotId,
			        aoi.status)
			    FROM AssignedOrderItem aoi
			    JOIN Product p ON aoi.productId = p.productId
			    JOIN Bay b ON aoi.bayId = b.bayId
			    AND aoi.bayId = :bayId
			    AND aoi.status = :status
			""")
	Page<PickedOrderItemResponse> findAllPickedOrderItemsByBay(@Param("bayId") UUID bayId, String status,
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
			WHERE a.status = 'PICKED' AND (o.status = 'PICK_COMPLETE' OR o.status = 'DELIVERING')
			""")
	List<Item> getAllItems();

	@Query("""
			     SELECT new openerp.openerpresourceserver.dto.response.DeliveryOrderItemResponse(
			        a.assignedOrderItemId,
			        a.orderId,
			        p.name,
			        p.weight,
			        a.quantity,
			        b.code,
			        a.lotId)
			    FROM AssignedOrderItem a
			    JOIN Product p ON a.productId = p.productId
			    JOIN Bay b ON a.bayId = b.bayId
			    WHERE a.assignedOrderItemId IN :assignedOrderItemIds
			""")
	List<DeliveryOrderItemResponse> findDeliveryOrderItemsByIds(List<UUID> assignedOrderItemIds);

	@Query("SELECT COUNT(a) FROM AssignedOrderItem a WHERE a.orderId = :orderId")
	long countAssignedItems(@Param("orderId") UUID orderId);

	List<AssignedOrderItem> findByOrderId(UUID orderId);

	long countByOrderIdAndStatusNot(UUID orderId, String status);

}
