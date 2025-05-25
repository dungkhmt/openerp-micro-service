package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.projection.PutawayItemProjection;
import openerp.openerpresourceserver.projection.ReceiptItemProjection;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, UUID> {

	@Query("""
			 SELECT r.quantity AS quantity,
			        b.code AS bayCode,
			        r.importPrice AS importPrice,
			        r.expiredDate AS expiredDate,
			        r.lotId AS lotId
			 FROM ReceiptItem r
			 JOIN Bay b ON r.bayId = b.bayId
			 WHERE r.receiptItemRequestId = :receiptItemRequestId
			""")
	List<ReceiptItemProjection> findByReceiptItemRequestId(@Param("receiptItemRequestId") UUID receiptItemRequestId);

	@Query("""
			    SELECT
			        r.receiptItemId AS receiptItemId,
			        p.name AS productName,
			        r.quantity AS quantity,
			        r.lotId AS lotId,
			        b.code AS bayCode,
			        r.status AS status
			    FROM ReceiptItem r
			    JOIN Product p ON r.productId = p.productId
			    JOIN Bay b ON r.bayId = b.bayId
			    WHERE  r.bayId = :bayId
			    AND r.status = :status
			""")
	Page<PutawayItemProjection> findPutawayItems(@Param("bayId") UUID bayId, @Param("status") String status,
			Pageable pageable);

	long countByReceiptIdAndStatusNot(UUID receiptId, String status);
}
