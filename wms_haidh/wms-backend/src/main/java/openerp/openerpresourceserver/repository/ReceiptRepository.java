package openerp.openerpresourceserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.projection.ReceiptInfoProjection;
import openerp.openerpresourceserver.projection.ReceiptProjection;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

	@Query(value = "SELECT r.receiptId AS receiptId, " + "r.receiptName AS receiptName, " + "w.name AS warehouseName, "
			+ "r.expectedReceiptDate AS expectedReceiptDate, " + "r.status AS status, " + "r.createdBy AS createdBy, "
			+ "r.approvedBy AS approvedBy, " + "r.cancelledBy AS cancelledBy " + "FROM Receipt r "
			+ "JOIN Warehouse w ON r.warehouseId = w.warehouseId " + "WHERE r.status = :status "
			+ "ORDER BY r.lastUpdatedStamp DESC")
	Page<ReceiptInfoProjection> findReceiptsByStatus(@Param("status") String status, Pageable pageable);

	@Query("SELECT r.receiptName AS receiptName, " + "r.description AS description, " + "r.receiptDate AS receiptDate, "
			+ "w.name AS warehouseName, " + "r.createdReason AS createdReason, "
			+ "r.expectedReceiptDate AS expectedReceiptDate, " + "r.status AS status, " + "r.createdBy AS createdBy, "
			+ "r.createdStamp AS createdStamp " + "FROM Receipt r "
			+ "JOIN Warehouse w ON r.warehouseId = w.warehouseId " + "WHERE r.receiptId = :id")
	Optional<ReceiptProjection> findReceiptDetailsById(@Param("id") UUID id);
}
