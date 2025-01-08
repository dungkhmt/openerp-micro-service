package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.entity.projection.ReceiptItemProjection;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, UUID> {

    @Query("""
            SELECT r.quantity AS quantity,
                   b.code AS bayCode,
                   r.importPrice AS importPrice,
                   r.expiredDate AS expiredDate,
                   r.lotId AS lotId,
                   rb.receiptBillId AS receiptBillId
            FROM ReceiptItem r
            JOIN Bay b ON r.bayId = b.bayId
            LEFT JOIN ReceiptBill rb ON r.receiptBillId = rb.receiptBillId
            WHERE r.receiptItemRequestId = :receiptItemRequestId
           """)
    List<ReceiptItemProjection> findByReceiptItemRequestId(@Param("receiptItemRequestId") UUID receiptItemRequestId);
}


