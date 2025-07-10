package com.hust.wmsbackend.vrp.delivery;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DeliveryAddressDTO implements Comparable<DeliveryAddressDTO> {
    private String deliveryTripItemId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private int sequence; // for response purpose; if dto is request, sequence = null

    @Override
    public int compareTo(DeliveryAddressDTO o) {
        return deliveryTripItemId.compareTo(o.getDeliveryTripItemId());
    }
}
