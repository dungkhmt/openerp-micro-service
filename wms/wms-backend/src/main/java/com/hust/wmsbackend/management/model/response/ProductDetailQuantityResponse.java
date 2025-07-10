package com.hust.wmsbackend.management.model.response;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@ToString
public class ProductDetailQuantityResponse {
    private UUID warehouseId;
    private String warehouseName;
    private UUID bayId;
    private String code;
    private BigDecimal quantity;
    private BigDecimal importPrice;
    private String lotId;

    public ProductDetailQuantityResponse(
        UUID warehouseId,
        String warehouseName,
        UUID bayId,
        String code,
        BigDecimal quantity,
        BigDecimal importPrice,
        String lotId
    ) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.bayId = bayId;
        this.code = code;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.lotId = lotId;
    }
}
