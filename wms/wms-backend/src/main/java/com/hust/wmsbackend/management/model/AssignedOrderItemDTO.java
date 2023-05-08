package com.hust.wmsbackend.management.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AssignedOrderItemDTO {
    private UUID assignOrderItemId;
    private UUID productId;
    private String productName;
    private BigDecimal quantity;
    private String lotId;
    private UUID bayId;
    private String bayCode;
    private UUID warehouseId;
    private String warehouseName;
    private UUID customerAddressId;
    private String customerAddressName;
    private UUID orderId;
    private String status;
}
