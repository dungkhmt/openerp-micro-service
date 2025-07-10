package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.entity.Receipt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptProcessResponse {
    Receipt info;
    List<ProcessedItemModel> processedItems;
    List<RemainingItemResponse> remainingItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RemainingItemResponse {
        private UUID receiptItemRequestId;
        private UUID productId;
        private String productName;
        private BigDecimal quantity;
        private UUID warehouseId;
        private String warehouseName;
    }
}
