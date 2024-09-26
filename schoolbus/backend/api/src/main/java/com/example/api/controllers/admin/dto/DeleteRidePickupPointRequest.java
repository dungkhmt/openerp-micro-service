package com.example.api.controllers.admin.dto;

import java.util.List;
import lombok.Data;

@Data
public class DeleteRidePickupPointRequest {
    List<Long> ridePickupPointIds;
}
