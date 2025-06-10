package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.CategoryProfitDatapoint;
import openerp.openerpresourceserver.dto.response.CustomerOrderResponse;
import openerp.openerpresourceserver.dto.response.OrderResponse;
import openerp.openerpresourceserver.dto.response.ProfitDatapoint;
import openerp.openerpresourceserver.dto.response.RevenueDatapoint;
import openerp.openerpresourceserver.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.OrderResponse( o.orderId,
			       o.orderDate,
			       o.totalOrderCost,
			       o.customerName,
			       o.status,
			       o.approvedBy,
			       o.cancelledBy)
			FROM Order o
			WHERE (:status IS NULL OR o.status = :status)
			ORDER BY o.lastUpdatedStamp DESC
			""")
	Page<OrderResponse> findOrdersByStatus(@Param("status") String status, Pageable pageable);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.OrderResponse( o.orderId,
			       o.orderDate,
			       o.totalOrderCost,
			       o.customerName,
			       o.status,
			       o.approvedBy,
			       o.cancelledBy)
			FROM Order o
			WHERE o.userLoginId = :userLoginId
			ORDER BY o.lastUpdatedStamp DESC
			""")
	Page<OrderResponse> findOrdersByUserLoginId(@Param("userLoginId") String userLoginId, Pageable pageable);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.CustomerOrderResponse(
			        ca.customerAddressId,
			        o.customerName,
			        o.customerPhoneNumber,
			        ca.addressName,
			        ca.longitude,
			        ca.latitude)
			    FROM Order o
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE o.orderId = :orderId
			""")
	Optional<CustomerOrderResponse> findCustomerOrderById(@Param("orderId") UUID orderId);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.RevenueDatapoint(
			        FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM'),
			        SUM(o.totalProductCost))
			    FROM Order o
			    GROUP BY FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM')
			""")
	List<RevenueDatapoint> getMonthlyRevenue();

	@Query("""
			     SELECT new openerp.openerpresourceserver.dto.response.ProfitDatapoint(
			        FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM'),
			        SUM(a.quantity * (i.priceUnit - inv.importPrice)))
			    FROM AssignedOrderItem a
			    JOIN Order o ON a.orderId = o.orderId
			    JOIN SaleOrderItem i ON a.orderId = i.orderId AND a.productId = i.productId
			    JOIN InventoryItem inv ON a.inventoryItemId = inv.inventoryItemId
			    GROUP BY FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM')
			""")
	List<ProfitDatapoint> getMonthlyProfit();

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.CategoryProfitDatapoint(
			        c.name,
			        SUM(a.quantity * (i.priceUnit - inv.importPrice)))
			    FROM AssignedOrderItem a
			    JOIN SaleOrderItem i ON a.orderId = i.orderId AND a.productId = i.productId
			    JOIN InventoryItem inv ON a.inventoryItemId = inv.inventoryItemId
			    JOIN Product p ON a.productId = p.productId
			    JOIN ProductCategory c ON p.categoryId = c.categoryId
			    WHERE FUNCTION('to_char', a.lastUpdatedStamp, 'YYYY-MM') = :month
			    GROUP BY c.name
			    HAVING SUM(a.quantity * (i.priceUnit - inv.importPrice)) > 0
			    ORDER BY SUM(a.quantity * (i.priceUnit - inv.importPrice)) DESC
			""")
	List<CategoryProfitDatapoint> getMonthlyProfitByCategory(@Param("month") String month);

	@Modifying
	@Query("UPDATE Order o SET o.status = 'DELIVERING', o.lastUpdatedStamp = CURRENT_TIMESTAMP WHERE o.id IN :orderIds")
	int updateStatusToDelivering(@Param("orderIds") List<UUID> orderIds);

}
