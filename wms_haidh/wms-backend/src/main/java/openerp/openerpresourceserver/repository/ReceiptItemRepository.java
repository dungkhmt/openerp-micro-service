package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.PutawayItemResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemResponse;
import openerp.openerpresourceserver.entity.ReceiptItem;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, UUID> {

	@Query("""
			 SELECT new openerp.openerpresourceserver.dto.response.ReceiptItemResponse( r.quantity,
			        b.code,
			        r.importPrice,
			        r.expiredDate,
			        r.lotId)
			 FROM ReceiptItem r
			 JOIN Bay b ON r.bayId = b.bayId
			 WHERE r.receiptItemRequestId = :receiptItemRequestId
			""")
	List<ReceiptItemResponse> findByReceiptItemRequestId(@Param("receiptItemRequestId") UUID receiptItemRequestId);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.PutawayItemResponse(
			        r.receiptItemId,
			        p.name,
			        r.quantity,
			        r.lotId,
			        b.code,
			        r.status)
			    FROM ReceiptItem r
			    JOIN Product p ON r.productId = p.productId
			    JOIN Bay b ON r.bayId = b.bayId
			    WHERE  r.bayId = :bayId
			    AND r.status = :status
			""")
	Page<PutawayItemResponse> findPutawayItems(@Param("bayId") UUID bayId, @Param("status") String status,
			Pageable pageable);

	long countByReceiptIdAndStatusNot(UUID receiptId, String status);
}
