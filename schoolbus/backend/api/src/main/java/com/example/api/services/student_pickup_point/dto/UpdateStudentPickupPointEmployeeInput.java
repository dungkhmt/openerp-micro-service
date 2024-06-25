package com.example.api.services.student_pickup_point.dto;

import com.example.shared.enumeration.StudentPickupPointStatus;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStudentPickupPointEmployeeInput {
    private List<Long> studentIds;
    private Long pickupPointId;
    private StudentPickupPointStatus status;
    private Long rideId;
}
