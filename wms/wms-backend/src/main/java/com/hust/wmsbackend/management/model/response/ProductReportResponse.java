package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@ToString
public class ProductReportResponse {
    private String productId;
    private String productName;
    private BigDecimal totalImportPrice;
    private BigDecimal totalExportPrice;
    private BigDecimal totalQuantity;
    private BigDecimal price;

    public ProductReportResponse(UUID productId, String productName, BigDecimal totalImportPrice, BigDecimal totalQuantity) {
        this.productId = productId.toString();
        this.productName = productName;
        this.totalImportPrice = totalImportPrice;
        this.totalExportPrice = null;
        this.totalQuantity = totalQuantity;
    }
}
