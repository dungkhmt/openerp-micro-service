package openerp.openerpresourceserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.ReceiptInfoResponse;
import openerp.openerpresourceserver.dto.response.ReceiptResponse;
import openerp.openerpresourceserver.entity.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

	@Query(value = "SELECT new openerp.openerpresourceserver.dto.response.ReceiptInfoResponse( r.receiptId, " + "r.receiptName, " + "w.name, "
			+ "r.expectedReceiptDate, " + "r.status, " + "r.createdBy, "
			+ "r.approvedBy, " + "r.cancelledBy) " + "FROM Receipt r "
			+ "JOIN Warehouse w ON r.warehouseId = w.warehouseId " + "WHERE r.status = :status "
			+ "ORDER BY r.lastUpdatedStamp DESC")
	Page<ReceiptInfoResponse> findReceiptsByStatus(@Param("status") String status, Pageable pageable);

	@Query("SELECT new openerp.openerpresourceserver.dto.response.ReceiptResponse( r.receiptName, " + "r.description, " + "w.name, "
			+ "s.name, " + "r.expectedReceiptDate, " + "r.status, "
			+ "r.createdBy, " + "r.createdStamp) " + "FROM Receipt r "
			+ "JOIN Warehouse w ON r.warehouseId = w.warehouseId " + "JOIN Supplier s ON r.supplierId = s.supplierId "
			+ "WHERE r.receiptId = :id")
	Optional<ReceiptResponse> findReceiptDetailsById(@Param("id") UUID id);
}
