package com.hust.wmsbackend.vrp.delivery;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RouteRequest {
    private BigDecimal warehouseLon;
    private BigDecimal warehouseLat;
    private List<DeliveryAddressDTO> addressDTOs;
}
