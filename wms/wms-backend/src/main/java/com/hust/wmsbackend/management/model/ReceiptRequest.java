package com.hust.wmsbackend.management.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReceiptRequest {
    private String warehouseId;
    private Date receivedDate;
    private String receiptName;
    private String description;
    private List<ReceiptItemRequest> receiptItemList;
    private String createdReason;
    private Date expectedReceiveDate;
    private int isPurchaseManagerRequest;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReceiptItemRequest {
        private String productId;
        private String lotId;
        private String bayId;
        private BigDecimal quantity;
        private BigDecimal importPrice;
        private Date expiredDate;
        private String warehouseId;
    }

}
