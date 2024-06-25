package com.example.api.controllers.admin.dto;

import com.example.api.services.ride_pickup_point.dto.AddRidePickupPointInput;
import java.util.List;
import lombok.Data;

@Data
public class AddRidePickupPointRequest {
    List<ItemRequest> items;

    public AddRidePickupPointInput toInput() {
        return AddRidePickupPointInput.builder()
                .items(items.stream().map(ItemRequest::toInput).toList())
                .build();
    }

    public static class ItemRequest {
        private Long pickupPointId;
        private Long rideId;

        public AddRidePickupPointInput.ItemInput toInput() {
            return AddRidePickupPointInput.ItemInput.builder()
                    .pickupPointId(pickupPointId)
                    .rideId(rideId)
                    .build();
        }
    }
}
