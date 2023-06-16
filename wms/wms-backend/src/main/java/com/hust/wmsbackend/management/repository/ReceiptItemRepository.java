package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.ReceiptItem;
import com.hust.wmsbackend.management.model.response.ProcessedItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, UUID> {

    String GET_PROCESSED_ITEMS = "select new com.hust.wmsbackend.management.model.response.ProcessedItemModel " +
            "(ri.receiptItemId, prod.productId, prod.name, ri.quantity, b.bayId, b.code, w.warehouseId, w.name, ri.lotId, " +
            "ri.importPrice, ri.expiredDate, ri.receiptItemRequestId) " +
            "from ReceiptItem ri " +
            "join Product prod on prod.productId = ri.productId " +
            "join Bay b on ri.bayId = b.bayId " +
            "join Warehouse w on w.warehouseId = b.warehouseId ";

    List<ReceiptItem> findAllByReceiptId(UUID id);

    @Query(GET_PROCESSED_ITEMS + "where ri.receiptId = :receiptId")
    List<ProcessedItemModel> getProcessedItemsByReceiptId(UUID receiptId);

    @Query(GET_PROCESSED_ITEMS + "where ri.receiptBillId = :receiptBillId")
    List<ProcessedItemModel> getProcessedItemsByReceiptBillId(String receiptBillId);

    Optional<ReceiptItem> findReceiptItemByReceiptItemRequestId(UUID requestId);
}
