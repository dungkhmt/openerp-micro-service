package com.hust.wmsbackend.management.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CustomerHistoryResponse {
    private String customerId;
    private BigDecimal totalSuccessOrderCount;
    private BigDecimal totalSuccessOrderCost;
    private BigDecimal totalCancelledOrderCount;
    private BigDecimal totalCancelledOrderCost;

    public CustomerHistoryResponse(
        String customerId,
        BigDecimal totalSuccessOrderCount,
        BigDecimal totalSuccessOrderCost,
        BigDecimal totalCancelledOrderCount,
        BigDecimal totalCancelledOrderCost
    ) {
        this.customerId = customerId;
        this.totalSuccessOrderCount = totalSuccessOrderCount;
        this.totalSuccessOrderCost = totalSuccessOrderCost;
        this.totalCancelledOrderCount = totalCancelledOrderCount;
        this.totalCancelledOrderCost = totalCancelledOrderCost;
    }
}
