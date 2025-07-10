package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductWarehouseResponse {
    private String warehouseId;
    private List<ProductWarehouseDetailResponse> products;
    private BigDecimal totalImportPrice;
    private BigDecimal totalExportPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductWarehouseDetailResponse {
        private String productId;
        private String productName;
        private BigDecimal quantity;
        private String lotId;
        private String bayId;
        private String bayCode;
        private BigDecimal importPrice;
        private BigDecimal exportPrice;
    }
}
