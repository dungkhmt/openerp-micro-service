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

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.projection.CategoryProfitDatapoint;
import openerp.openerpresourceserver.projection.CustomerOrderProjection;
import openerp.openerpresourceserver.projection.OrderProjection;
import openerp.openerpresourceserver.projection.ProfitDatapoint;
import openerp.openerpresourceserver.projection.RevenueDatapoint;

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
			SELECT o.orderId AS orderId,
			       o.orderDate AS orderDate,
			       o.totalOrderCost AS totalOrderCost,
			       o.customerName AS customerName,
			       o.status AS status,
			       o.approvedBy AS approvedBy,
			       o.cancelledBy AS cancelledBy
			FROM Order o
			WHERE o.userLoginId = :userLoginId
			ORDER BY o.lastUpdatedStamp DESC
			""")
	Page<OrderProjection> findOrdersByUserLoginId(@Param("userLoginId") String userLoginId, Pageable pageable);

	@Query("""
			    SELECT
			        o.customerName AS customerName,
			        o.customerPhoneNumber AS customerPhoneNumber,
			        ca.addressName AS addressName,
			        ca.longitude AS longitude,
			        ca.latitude AS latitude
			    FROM Order o
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE o.orderId = :orderId
			""")
	Optional<CustomerOrderProjection> findCustomerOrderById(@Param("orderId") UUID orderId);

	@Query("""
			    SELECT
			        FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM') AS date,
			        SUM(o.totalProductCost) AS revenue
			    FROM Order o
			    GROUP BY FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM')
			""")
	List<RevenueDatapoint> getMonthlyRevenue();

	@Query("""
			    SELECT
			        FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM') AS date,
			        SUM(a.quantity * (i.priceUnit - inv.importPrice)) AS profit
			    FROM AssignedOrderItem a
			    JOIN Order o ON a.orderId = o.orderId
			    JOIN SaleOrderItem i ON a.orderId = i.orderId AND a.productId = i.productId
			    JOIN InventoryItem inv ON a.inventoryItemId = inv.inventoryItemId
			    GROUP BY FUNCTION('to_char', o.lastUpdatedStamp, 'YYYY-MM')
			""")
	List<ProfitDatapoint> getMonthlyProfit();

	@Query("""
			    SELECT
			        c.name AS label,
			        SUM(a.quantity * (i.priceUnit - inv.importPrice)) AS profit
			    FROM AssignedOrderItem a
			    JOIN SaleOrderItem i ON a.orderId = i.orderId AND a.productId = i.productId
			    JOIN InventoryItem inv ON a.inventoryItemId = inv.inventoryItemId
			    JOIN Product p ON a.productId = p.productId
			    JOIN ProductCategory c ON p.categoryId = c.categoryId
			    WHERE FUNCTION('to_char', a.lastUpdatedStamp, 'YYYY-MM') = :month
			    GROUP BY c.name
			    HAVING SUM(a.quantity * (i.priceUnit - inv.importPrice)) > 0
			    ORDER BY profit DESC
			""")
	List<CategoryProfitDatapoint> getMonthlyProfitByCategory(@Param("month") String month);
	
	@Modifying
	@Query("UPDATE Order o SET o.status = 'DELIVERING', o.lastUpdatedStamp = CURRENT_TIMESTAMP WHERE o.id IN :orderIds")
	int updateStatusToDelivering(@Param("orderIds") List<UUID> orderIds);



}
