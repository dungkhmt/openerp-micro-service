package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ReceiptBill;
import openerp.openerpresourceserver.projection.ReceiptBillProjection;

@Repository
public interface ReceiptBillRepository extends JpaRepository<ReceiptBill, String> {

    @Query(value = "SELECT rb.receiptBillId AS receiptBillId, " +
                   "rb.description AS description, " +
                   "rb.createdBy AS createdBy, " +
                   "rb.totalPrice AS totalPrice, " +
                   "r.receiptName AS receiptName " +
                   "FROM ReceiptBill rb " +
                   "JOIN Receipt r ON rb.receiptId = r.receiptId " +  
                   "ORDER BY rb.lastUpdateStamp DESC", 
           countQuery = "SELECT COUNT(rb.receiptBillId) " +
                        "FROM ReceiptBill rb " +
                        "JOIN Receipt r ON rb.receiptId = r.receiptId")
    Page<ReceiptBillProjection> findAllReceiptBills(Pageable pageable);
    
    @Query(value = "SELECT rb.receiptBillId AS receiptBillId FROM ReceiptBill rb WHERE rb.receiptId = :receiptId")
    List<String> findReceiptBillIdsByReceiptId(@Param("receiptId") UUID receiptId);
}

