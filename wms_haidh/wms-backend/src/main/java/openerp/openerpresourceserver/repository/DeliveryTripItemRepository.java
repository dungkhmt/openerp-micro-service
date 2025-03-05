package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.DeliveryTripItem;
import openerp.openerpresourceserver.entity.projection.CustomerDeliveryProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryItemDetailProjection;

@Repository
public interface DeliveryTripItemRepository extends JpaRepository<DeliveryTripItem, String> {
	@Query("""
			    SELECT DISTINCT
			        o.orderId AS orderId,
			        o.customerName AS customerName,
			        o.customerPhoneNumber AS customerPhoneNumber,
			        ca.addressName AS customerAddress,
			        dti.sequence AS sequence
			    FROM DeliveryTripItem dti
			    JOIN Order o ON dti.orderId = o.orderId
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE dti.isDeleted = false
			      AND dti.deliveryTripId = :deliveryTripId
			    ORDER BY dti.sequence
			""")
	List<CustomerDeliveryProjection> findCustomersByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);

	@Query("""
			    SELECT
			        p.name AS productName,
			        p.weight AS weight,
			        dti.quantity AS quantity,
			        b.code AS bayCode,
			        aoi.lotId AS lotId
			    FROM DeliveryTripItem dti
			    JOIN AssignedOrderItem aoi ON dti.assignedOrderItemId = aoi.assignedOrderItemId
			    JOIN Product p ON aoi.productId = p.productId
			    LEFT JOIN Bay b ON aoi.bayId = b.bayId
			    WHERE dti.deliveryTripId = :deliveryTripId
			      AND aoi.orderId = :orderId
			""")
	Page<DeliveryItemDetailProjection> findDeliveryItemsByTripAndOrder(@Param("deliveryTripId") String deliveryTripId,
			@Param("orderId") UUID orderId, Pageable pageable);
}
