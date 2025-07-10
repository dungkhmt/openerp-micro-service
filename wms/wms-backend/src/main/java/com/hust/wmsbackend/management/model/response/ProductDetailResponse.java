package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private Product productInfo;
    private List<ProductDetailQuantityResponse> quantityList; // TODO: consider remove this field
    private List<ProductWarehouseQuantity> warehouseQuantities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductWarehouseQuantity {
        private String warehouseId;
        private String warehouseName;
        private BigDecimal quantity;
    }
}
