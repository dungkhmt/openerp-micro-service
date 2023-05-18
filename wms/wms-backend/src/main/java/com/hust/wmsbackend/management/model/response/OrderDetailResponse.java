package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderDetailResponse {
    private String userLoginId;
    private String customerName;
    private BigDecimal totalSuccessProductCount;
    private BigDecimal totalSuccessProductCost;
    private BigDecimal totalFailProductCount;
    private BigDecimal totalFailProductCost;
    private List<OrderHistoryResponse> successProductHistory;
    private List<OrderHistoryResponse> failProductHistory;
    private String createdDate;
    private String paymentMethod;
    private String receiptAddress;
    private BigDecimal totalOrderCost;
    private List<OrderItemResponse> items;
    private List<OrderItemResponse> remainingItems;
    private List<ProcessedOrderItemResponse> processedItems;
    private String status;
    private String statusCode;
    private BigDecimal cusAddLon;
    private BigDecimal cusAddLat;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private UUID productId;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal priceUnit;
        private String deliveryStatus;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProcessedOrderItemResponse {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderHistoryResponse {
        private String orderId;
        private String productId;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal priceUnit;
        private String address;
        private String createdDate;
    }
}
