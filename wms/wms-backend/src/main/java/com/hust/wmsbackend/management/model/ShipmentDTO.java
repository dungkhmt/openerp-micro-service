package com.hust.wmsbackend.management.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ShipmentDTO {
    private String shipmentId;
    private String createdStamp;
    private String lastUpdatedStamp;
    private String createdBy;
    private Date expectedDeliveryStamp; // for create shipment request -> this field only

    private List<DeliveryTripDTO> trips;
}
