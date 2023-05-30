package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductCategoryMonthlyData {
    private String time;
    private String label; // category name
    private BigDecimal profit;
    private BigDecimal y; // percentage

    public ProductCategoryMonthlyData(String time, String category, BigDecimal profit) {
        this.time = time;
        this.label = category;
        this.profit = profit;
        this.y = null;
    }
}
