package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.CustomerDeliveryResponse;
import openerp.openerpresourceserver.dto.response.DeliveryItemDetailResponse;
import openerp.openerpresourceserver.entity.DeliveryTripItem;

@Repository
public interface DeliveryTripItemRepository extends JpaRepository<DeliveryTripItem, UUID> {
	@Query("""
			    SELECT DISTINCT new openerp.openerpresourceserver.dto.response.CustomerDeliveryResponse(
			        o.orderId,
			        o.customerName,
			        o.customerPhoneNumber,
			        ca.addressName,
			        dti.sequence,
			        dti.status)
			    FROM DeliveryTripItem dti
			    JOIN Order o ON dti.orderId = o.orderId
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE dti.deliveryTripId = :deliveryTripId
			    ORDER BY dti.sequence
			""")
	List<CustomerDeliveryResponse> findCustomersByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);

	@Query("""
			   SELECT new openerp.openerpresourceserver.dto.response.DeliveryItemDetailResponse(
			        dti.deliveryTripItemId ,
			        p.name,
			        p.weight,
			        dti.quantity,
			        p.uom,
			        b.code,
			        aoi.lotId)
			    FROM DeliveryTripItem dti
			    JOIN AssignedOrderItem aoi ON dti.assignedOrderItemId = aoi.assignedOrderItemId
			    JOIN Product p ON aoi.productId = p.productId
			    LEFT JOIN Bay b ON aoi.bayId = b.bayId
			    WHERE dti.deliveryTripId = :deliveryTripId
			      AND aoi.orderId = :orderId
			""")
	Page<DeliveryItemDetailResponse> findDeliveryItemsByTripAndOrder(@Param("deliveryTripId") String deliveryTripId,
			@Param("orderId") UUID orderId, Pageable pageable);

	@Query("SELECT d.assignedOrderItemId FROM DeliveryTripItem d WHERE d.deliveryTripId = :deliveryTripId")
	List<UUID> findIdsByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);

	@Query("SELECT DISTINCT d.orderId FROM DeliveryTripItem d WHERE d.deliveryTripId = :deliveryTripId")
	List<UUID> findOrderIdsByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);

	@Modifying
	@Query("UPDATE DeliveryTripItem d SET d.status = 'DELIVERED', d.lastUpdatedStamp = CURRENT_TIMESTAMP "
			+ "WHERE d.deliveryTripId = :deliveryTripId AND d.orderId = :orderId")
	int markItemsAsDelivered(@Param("deliveryTripId") String deliveryTripId, @Param("orderId") UUID orderId);

	@Query("SELECT COUNT(d) FROM DeliveryTripItem d " + "WHERE d.deliveryTripId = :tripId "
			+ "AND d.status <> 'DELIVERED' ")
	long countUndeliveredItems(@Param("tripId") String tripId);

	@Query("SELECT COUNT(d) FROM DeliveryTripItem d " + "WHERE d.orderId = :orderId AND d.status = 'DELIVERED' ")
	long countDeliveredItemsByOrderId(@Param("orderId") UUID orderId);

}
