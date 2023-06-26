package com.hust.wmsbackend.management.model.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryTripItemSuggestRequest {
    private List<String> customerAddressIds;
    private String warehouseId;
}
