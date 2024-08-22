package com.hust.wmsbackend.management.model.model2.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AssignedOrderItemRequest2 {
    @NotNull
    private UUID orderId;
    @Valid
    private List<AssignedOrderItemRequestDetail> items;
    private boolean done;

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @ToString
    public static class AssignedOrderItemRequestDetail {
        @NotNull
        private UUID productId;
        @NotNull
        private UUID bayId;
        @NotNull
        private BigDecimal quantity;
        @NotNull
        private UUID warehouseId;

        private String productName;
        private String warehouseName;
        private String bayCode;
    }
}
