package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.SaleOrderItemDetailResponse;
import openerp.openerpresourceserver.dto.response.SaleOrderItemResponse;
import openerp.openerpresourceserver.entity.SaleOrderItem;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, UUID> {
	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.SaleOrderItemResponse( soi.saleOrderItemId,
			           p.name,
			           soi.quantity,
			           p.uom AS uom,
			           soi.priceUnit,
			           soi.completed)
			    FROM SaleOrderItem soi
			    JOIN Product p ON soi.productId = p.productId
			    WHERE soi.orderId = :orderId
			    ORDER BY soi.lastUpdated DESC
			""")
	List<SaleOrderItemResponse> findSaleOrderItems(@Param("orderId") UUID id);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.SaleOrderItemDetailResponse( p.name,
			           soi.quantity,
			           soi.priceUnit,
			           p.uom,
			           soi.completed,
			           ca.addressName)
			    FROM SaleOrderItem soi
			    JOIN Product p ON soi.productId = p.productId
			    JOIN Order o ON soi.orderId = o.orderId
			    JOIN CustomerAddress ca ON o.customerAddressId = ca.customerAddressId
			    WHERE soi.saleOrderItemId = :id
			""")
	SaleOrderItemDetailResponse findSaleOrderItemDetailById(@Param("id") UUID id);

	@Query("""
			    SELECT soi.productId
			    FROM SaleOrderItem soi
			    WHERE soi.saleOrderItemId = :saleOrderItemId
			""")
	Optional<UUID> findProductIdBySaleOrderItemId(@Param("saleOrderItemId") UUID saleOrderItemId);

	@Query("""
			    SELECT soi.quantity
			    FROM SaleOrderItem soi
			    WHERE soi.saleOrderItemId = :saleOrderItemId
			""")
	Optional<Integer> findOriginalQuantityBySaleOrderItemId(@Param("saleOrderItemId") UUID saleOrderItemId);
}
