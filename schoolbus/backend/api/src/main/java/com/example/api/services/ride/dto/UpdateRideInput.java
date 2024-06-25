package com.example.api.services.ride.dto;

import com.example.shared.enumeration.RideStatus;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Dont allow to update isToSchool field
 */
@Data
@Builder
public class UpdateRideInput {
    private Long id;
    private Long busId;
    private Instant startAt;
    private Instant endAt;
    private String startFrom;
    private RideStatus status;
}
