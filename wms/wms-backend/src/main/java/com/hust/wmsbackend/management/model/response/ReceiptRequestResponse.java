package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptRequestResponse {
    private UUID receiptRequestId;
    private String createdDate;
    private String approvedBy;
    private String status;
    private String createdBy;
    private String createdReason;
    private String cancelledBy;
    private String lastUpdateStamp;
    private String expectedReceiveDate;
    List<ReceiptRequestItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReceiptRequestItemResponse {
        private UUID receiptRequestItemId;
        private UUID productId;
        private String productName;
        private BigDecimal quantity;
        private UUID warehouseId;
        private String warehouseName;
    }
}
