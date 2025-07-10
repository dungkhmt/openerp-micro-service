package com.hust.wmsbackend.vrp.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressPair {
    private BigDecimal sourLon;
    private BigDecimal sourLat;
    private BigDecimal destLon;
    private BigDecimal destLat;
}
