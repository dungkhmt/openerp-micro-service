package com.example.api.services.ride_pickup_point.dto;

import com.example.shared.enumeration.RidePickupPointStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRidePickupPointEmployeeInput {
    private Long rideId;
    private Long pickupPointId;
    private RidePickupPointStatus status;
}
