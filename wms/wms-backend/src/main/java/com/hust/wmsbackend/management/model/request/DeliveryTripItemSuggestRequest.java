package com.hust.wmsbackend.management.model.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DeliveryTripItemSuggestRequest {
    private List<String> assignedOrderItemIds;
    private String warehouseId;
}
