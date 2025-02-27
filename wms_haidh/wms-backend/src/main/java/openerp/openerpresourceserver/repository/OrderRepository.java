package openerp.openerpresourceserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.entity.projection.OrderProjection;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
	@Query("""
			SELECT o.orderId AS orderId,
			       o.orderDate AS orderDate,
			       o.totalOrderCost AS totalOrderCost,
			       o.customerName AS customerName,
			       o.status AS status,
			       o.approvedBy AS approvedBy,
			       o.cancelledBy AS cancelledBy
			FROM Order o
			WHERE (:status IS NULL OR o.status = :status)
			ORDER BY o.lastUpdatedStamp DESC
			""")
	Page<OrderProjection> findOrdersByStatus(@Param("status") String status, Pageable pageable);

	@Query("""
			    SELECT
			        o.customerName AS customerName,
			        ca.addressName AS addressName,
			        ca.longitude AS longitude,
			        ca.latitude AS latitude,
			        o.totalOrderCost AS totalOrderCost,
			        o.paymentType AS paymentType
			    FROM Order o
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE o.orderId = :orderId
			""")
	Optional<CustomerOrderProjection> findCustomerOrderById(@Param("orderId") UUID orderId);
}
