package com.example.api.services.ride_pickup_point.dto;

import com.example.shared.enumeration.RidePickupPointStatus;
import lombok.Data;

@Data
public class RidePickupPointFilterParam {
    private Long rideId;
    private Long pickupPointId;
    private RidePickupPointStatus status;
}
