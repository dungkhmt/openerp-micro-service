package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductPriceResponse {
    private UUID productId;
    private String productName;
    private BigDecimal currPrice;
    private List<ProductHistoryPrices> historyPrices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductHistoryPrices {
        private UUID productPriceId;
        private Date startDate;
        private Date endDate;
        private BigDecimal price;
        private String description;
    }
}
