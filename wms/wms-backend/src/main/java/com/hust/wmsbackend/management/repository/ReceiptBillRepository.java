package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.ReceiptBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceiptBillRepository extends JpaRepository<ReceiptBill, String> {

    List<ReceiptBill> findAllByReceiptId(UUID receiptID);

    Optional<ReceiptBill> findFirstByReceiptIdOrderByCreatedStampDesc(UUID receiptId);

}
