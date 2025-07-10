package com.hust.wmsbackend.management.model.model2.response;

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
public class AssignedOrderItemResponse {
    private UUID orderId;
    private List<AssignedOrderItem> assignedOrderItemList;

    @Data
    @AllArgsConstructor
    @Builder
    public static class AssignedOrderItem {
        private UUID productId;
        private String productName;
        private BigDecimal quantity;
        private UUID bayId;
        private String bayCode;
        private UUID warehouseId;
        private String warehouseName;
        private String lotId;
        private String status;
        private String createdDate;
    }

}
