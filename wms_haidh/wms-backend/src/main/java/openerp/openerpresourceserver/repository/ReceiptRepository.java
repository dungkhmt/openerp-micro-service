package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.entity.projection.ReceiptInfoProjection;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

	@Query(value = "SELECT r.receiptId AS receiptId, " + "r.receiptName AS receiptName, "
			+ "r.createdReason AS createdReason, " + "r.description AS description, "
			+ "r.expectedReceiptDate AS expectedReceiptDate, " + "r.status AS status, " + "r.createdBy AS createdBy, "
			+ "r.approvedBy AS approvedBy, " + "r.cancelledBy AS cancelledBy " + "FROM Receipt r "
			+ "WHERE r.status = :status "
			+ "ORDER BY r.lastUpdatedStamp DESC", countQuery = "SELECT COUNT(r.receiptId) " + "FROM Receipt r "
					+ "WHERE r.status = :status")
	Page<ReceiptInfoProjection> findReceiptsByStatus(@Param("status") String status, Pageable pageable);
}
