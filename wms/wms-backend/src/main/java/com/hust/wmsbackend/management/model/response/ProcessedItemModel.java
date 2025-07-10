package com.hust.wmsbackend.management.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
public class ProcessedItemModel {
    private UUID receiptItemId;
    private UUID productId;
    private String productName;
    private BigDecimal quantity;
    private UUID bayId;
    private String bayCode;
    private UUID warehouseId;
    private String warehouseName;
    private String lotId;
    private BigDecimal importPrice;
    private Date expiredDate;
    private UUID receiptItemRequestId;

    public ProcessedItemModel(UUID receiptItemId, UUID productId, String productName, BigDecimal quantity,
                              UUID bayId, String bayCode, UUID warehouseId, String warehouseName, String lotId,
                              BigDecimal importPrice, Date expiredDate, UUID receiptItemRequestId) {
        this.receiptItemId = receiptItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.bayId = bayId;
        this.bayCode = bayCode;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.lotId = lotId;
        this.importPrice = importPrice;
        this.expiredDate = expiredDate;
        this.receiptItemRequestId = receiptItemRequestId;
    }
}
