package com.example.api.controllers.employee.dto;

import com.example.api.services.student_pickup_point.dto.UpdateStudentPickupPointEmployeeInput;
import com.example.shared.enumeration.StudentPickupPointStatus;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStudentPickupPointRequest {
    private List<Long> studentIds;
    private Long pickupPointId;
    private StudentPickupPointStatus status;
    private Long rideId;

    public UpdateStudentPickupPointEmployeeInput toInput() {
        return UpdateStudentPickupPointEmployeeInput.builder()
                .studentIds(studentIds)
                .pickupPointId(pickupPointId)
                .status(status)
                .rideId(rideId)
                .build();
    }
}
