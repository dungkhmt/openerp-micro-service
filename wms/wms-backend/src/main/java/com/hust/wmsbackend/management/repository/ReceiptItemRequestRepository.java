package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.ReceiptItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReceiptItemRequestRepository extends JpaRepository<ReceiptItemRequest, UUID> {

    List<ReceiptItemRequest> findAllByReceiptId(UUID receiptId);

    List<ReceiptItemRequest> findAllByProductId(UUID productId);

    List<ReceiptItemRequest> findAllByWarehouseId(UUID warehouseId);
}
