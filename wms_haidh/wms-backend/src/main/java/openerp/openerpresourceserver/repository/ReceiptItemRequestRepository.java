package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ReceiptItemRequest;
import openerp.openerpresourceserver.projection.ReceiptItemRequestDetailProjection;
import openerp.openerpresourceserver.projection.ReceiptItemRequestProjection;

@Repository
public interface ReceiptItemRequestRepository extends JpaRepository<ReceiptItemRequest, UUID> {

	Optional<ReceiptItemRequest> findByReceiptItemRequestId(UUID receiptItemRequestId);

	@Query(value = """
			 SELECT r.receiptItemRequestId AS receiptItemRequestId,
			        r.quantity AS quantity,
			        r.completed AS completed,
			        p.name AS productName,
			        p.uom AS uom
			 FROM ReceiptItemRequest r
			 JOIN Product p ON r.productId = p.productId
			 WHERE r.receiptId = :receiptId
			 ORDER BY r.lastUpdated DESC
			""")
	List<ReceiptItemRequestProjection> findAllWithDetails(@Param("receiptId") UUID id);

	@Query("""
			 SELECT r.quantity AS quantity,
			        r.completed AS completed,
			        p.name AS productName,
			        p.uom AS uom,
			        w.name AS warehouseName
			 FROM ReceiptItemRequest r
			 JOIN Product p ON r.productId = p.productId
			 JOIN Warehouse w ON r.warehouseId = w.warehouseId
			 WHERE r.receiptItemRequestId = :receiptItemRequestId
			""")
	Optional<ReceiptItemRequestDetailProjection> findDetailById(@Param("receiptItemRequestId") UUID receiptItemRequestId);

}
