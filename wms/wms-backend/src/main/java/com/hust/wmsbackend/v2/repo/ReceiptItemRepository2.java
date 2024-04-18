package com.hust.wmsbackend.v2.repo;

import com.hust.wmsbackend.management.entity.ReceiptItem;
import com.hust.wmsbackend.management.model.response.ProcessedItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiptItemRepository2 extends JpaRepository<ReceiptItem, UUID> {

    String GET_PROCESSED_ITEMS = "select new com.hust.wmsbackend.management.model.response.ProcessedItemModel " +
            "(rci.receiptItemId, prod.productId, prod.name, rci.quantity, b.bayId, b.code, w.warehouseId, w.name, rci.lotId, " +
            "rci.importPrice, rci.expiredDate, rci.receiptItemRequestId) " +
            "from ReceiptItem rci " +
            "join Product prod on prod.productId = rci.productId " +
            "join Bay b on b.bayId = rci.bayId " +
            "join Warehouse w on w.warehouseId = b.warehouseId ";

    List<ReceiptItem> findAllByReceiptId(UUID id);

    @Query(GET_PROCESSED_ITEMS + "where rci.receiptId = :receiptId")
    List<ProcessedItemModel> getProcessedItemsByReceiptId(UUID receiptId);

    @Query(GET_PROCESSED_ITEMS + "where rci.receiptBillId = :receiptBillId")
    List<ProcessedItemModel> getProcessedItemsByReceiptBillId(String receiptBillId);

    List<ReceiptItem> findAllByProductId(UUID productId);

    List<ReceiptItem> findAllByBayId(UUID bayId);
}
