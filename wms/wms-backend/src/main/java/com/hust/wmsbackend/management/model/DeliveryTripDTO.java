package com.hust.wmsbackend.management.model;

import com.hust.wmsbackend.management.entity.DeliveryTrip;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DeliveryTripDTO {
    private String deliveryTripId;
    private String shipmentId;
    private UUID vehicleId;
    private String deliveryPersonId;
    private String deliveryPersonName;
    private BigDecimal distance;
    private BigDecimal totalWeight;
    private int totalLocations;
    private String lastUpdatedStamp;
    private String createdStamp;
    private String createdBy;
    private UUID warehouseId;
    private String warehouseName;
    private boolean isDeleted;
    // TODO: list delivery trip item
    private List<DeliveryTripItemDTO> items;
    private String deliveryTripStatus;
    private String deliveryTripStatusCode;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class DeliveryTripItemDTO {
        private String deliveryTripItemId;
        private UUID assignOrderItemId;
        private UUID productId;
        private String productName;
        private UUID bayId;
        private String bayCode;
        private UUID warehouseId;
        private String warehouseName;
        private BigDecimal quantity;
        private int sequence;
        private UUID orderId;
        private String lotId;
        private String customerAddressName;
        private String statusCode;
        private String customerName;
        private String customerPhone;
    }

    public DeliveryTripDTO(DeliveryTrip trip) {
        this.deliveryTripId = trip.getDeliveryTripId();
        this.shipmentId = trip.getShipmentId();
        this.vehicleId = trip.getVehicleId();
        this.deliveryPersonId = trip.getDeliveryPersonId();
        this.distance = trip.getDistance();
        this.totalWeight = trip.getTotalWeight();
        this.totalLocations = trip.getTotalLocations();
        this.lastUpdatedStamp = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, trip.getLastUpdatedStamp());
        this.createdStamp = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, trip.getCreatedStamp());
        this.createdBy = trip.getCreatedBy();
        this.warehouseId = trip.getWarehouseId();
        this.isDeleted = trip.isDeleted();
        if (trip.getStatus() != null) {
            if (trip.isDeleted()) {
                this.deliveryTripStatus = "Đã xóa";
                this.deliveryTripStatusCode = "DELETED";
            } else {
                this.deliveryTripStatus = trip.getStatus().getName();
                this.deliveryTripStatusCode = trip.getStatus().getCode();
            }
        }
    }
}
