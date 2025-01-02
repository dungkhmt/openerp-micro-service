package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ReceiptItemRequest;
import openerp.openerpresourceserver.entity.projection.ReceiptItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemRequestProjection;

@Repository
public interface ReceiptItemRequestRepository extends JpaRepository<ReceiptItemRequest, UUID> {

	@Query("SELECT p.name AS productName, " + "r.quantity AS quantity, " + "w.name AS warehouseName "
			+ "FROM ReceiptItemRequest r " + "JOIN Product p ON r.productId = p.productId "
			+ "JOIN Warehouse w ON r.warehouseId = w.warehouseId " + "WHERE r.receiptId = :receiptId")
	List<ReceiptItemDetailProjection> findReceiptItemDetails(@Param("receiptId") UUID receiptId);

	Optional<ReceiptItemRequest> findByReceiptItemRequestId(UUID receiptItemRequestId);

	@Query(value = """
			 SELECT r.receiptItemRequestId AS receiptItemRequestId,
			        rc.receiptName AS receiptName,
			        r.quantity AS quantity,
			        r.completed AS completed,
			        rc.expectedReceiptDate AS expectedReceiptDate,
			        w.name AS warehouseName,
			        p.name AS productName
			 FROM ReceiptItemRequest r
			 JOIN Warehouse w ON r.warehouseId = w.warehouseId
			 JOIN Receipt rc ON r.receiptId = rc.receiptId
			 JOIN Product p ON r.productId = p.productId
			 WHERE rc.status = :status
			 ORDER BY r.lastUpdated DESC
			""", countQuery = """
			 SELECT COUNT(r)
			 FROM ReceiptItemRequest r
			 JOIN Warehouse w ON r.warehouseId = w.warehouseId
			 JOIN Receipt rc ON r.receiptId = rc.receiptId
			 JOIN Product p ON r.productId = p.productId
			 WHERE  rc.status = :status
			""")
	Page<ReceiptItemRequestProjection> findAllWithDetails(@Param("status") String status, Pageable pageable);

	@Query("""
			 SELECT r.quantity AS quantity,
			        r.completed AS completed,
			        rc.expectedReceiptDate AS expectedReceiptDate,
			        w.name AS warehouseName,
			        p.name AS productName
			 FROM ReceiptItemRequest r
			 JOIN Warehouse w ON r.warehouseId = w.warehouseId
			 JOIN Receipt rc ON r.receiptId = rc.receiptId
			 JOIN Product p ON r.productId = p.productId
			 WHERE r.receiptItemRequestId = :receiptItemRequestId
			""")
	Optional<ReceiptItemRequestProjection> findDetailById(@Param("receiptItemRequestId") UUID receiptItemRequestId);

}
