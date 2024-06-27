package com.example.api.services.ride_pickup_point.dto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddRidePickupPointInput {
    List<ItemInput> items;

    @Builder
    @Data
    public static class ItemInput {
        private Long pickupPointId;
        private Long rideId;
    }
}
