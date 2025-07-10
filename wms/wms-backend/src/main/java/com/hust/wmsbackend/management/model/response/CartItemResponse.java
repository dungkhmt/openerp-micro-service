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
public class CartItemResponse {

    private List<Item> items;
    private BigDecimal totalProductCost;
    private BigDecimal deliveryFee;
    private BigDecimal totalOrderCost;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Item {
        private UUID productId;
        private byte[] imageData;
        private String imageContentType;
        private String name;
        private BigDecimal priceUnit;
        private long quantity;
    }

}
