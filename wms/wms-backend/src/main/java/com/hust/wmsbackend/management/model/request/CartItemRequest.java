package com.hust.wmsbackend.management.model.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartItemRequest {

    private List<Item> items;
    private BigDecimal longitude;
    private BigDecimal latitude;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Item {
        private String productId;
        private long quantity;
    }
}
