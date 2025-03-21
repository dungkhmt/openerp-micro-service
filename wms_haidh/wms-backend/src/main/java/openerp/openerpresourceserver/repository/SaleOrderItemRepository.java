package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.SaleOrderItem;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemProjection;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, UUID> {
	@Query("""
			    SELECT soi.saleOrderItemId AS saleOrderItemId,
			           p.name AS productName,
			           soi.quantity AS quantity,
			           soi.priceUnit AS priceUnit,
			           soi.completed AS completed
			    FROM SaleOrderItem soi
			    JOIN Product p ON soi.productId = p.productId
			    WHERE soi.orderId = :orderId
			    ORDER BY soi.lastUpdated DESC
			""")
	List<SaleOrderItemProjection> findSaleOrderItems(@Param("orderId") UUID id);

	@Query("""
			    SELECT p.name AS productName,
			           soi.quantity AS quantity,
			           soi.priceUnit AS priceUnit,
			           soi.completed AS completed
			    FROM SaleOrderItem soi
			    JOIN Product p ON soi.productId = p.productId
			    WHERE soi.saleOrderItemId = :id
			""")
	SaleOrderItemDetailProjection findSaleOrderItemDetailById(@Param("id") UUID id);

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
