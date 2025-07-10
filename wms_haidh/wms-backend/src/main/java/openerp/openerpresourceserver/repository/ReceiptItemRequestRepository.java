package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.ReceiptItemRequestDetailResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemRequestResponse;
import openerp.openerpresourceserver.entity.ReceiptItemRequest;

@Repository
public interface ReceiptItemRequestRepository extends JpaRepository<ReceiptItemRequest, UUID> {

	Optional<ReceiptItemRequest> findByReceiptItemRequestId(UUID receiptItemRequestId);

	@Query(value = """
			 SELECT new openerp.openerpresourceserver.dto.response.ReceiptItemRequestResponse( 
			        r.receiptItemRequestId,
			        r.quantity,
			        r.completed,
			        p.name,
			        p.uom)
			 FROM ReceiptItemRequest r
			 JOIN Product p ON r.productId = p.productId
			 WHERE r.receiptId = :receiptId
			 ORDER BY r.lastUpdated DESC
			""")
	List<ReceiptItemRequestResponse> findAllWithDetails(@Param("receiptId") UUID id);

	@Query("""
			 SELECT new openerp.openerpresourceserver.dto.response.ReceiptItemRequestDetailResponse(
			        r.receiptItemRequestId,
			        r.quantity,
			        r.completed,
			        p.name,
			        p.uom,
			        w.name)
			 FROM ReceiptItemRequest r
			 JOIN Product p ON r.productId = p.productId
			 JOIN Warehouse w ON r.warehouseId = w.warehouseId
			 WHERE r.receiptItemRequestId = :receiptItemRequestId
			""")
	Optional<ReceiptItemRequestDetailResponse> findDetailById(@Param("receiptItemRequestId") UUID receiptItemRequestId);

}
