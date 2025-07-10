package com.hust.wmsbackend.management.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@ToString
public class OrderGeneralResponse {
    private UUID orderId;
    private String createdOrderDate;
    private String orderType;
    private String status;
    private BigDecimal totalOrderCost;
}
