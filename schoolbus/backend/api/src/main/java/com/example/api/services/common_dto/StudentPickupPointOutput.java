package com.example.api.services.common_dto;

import com.example.shared.db.entities.StudentPickupPoint;
import com.example.shared.enumeration.StudentPickupPointStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentPickupPointOutput {
    private Long id;
    private Long studentId;
    private Long pickupPointId;
    private StudentPickupPointStatus status;
    private Instant getInAt;

    private Instant createdAt;
    private Instant updatedAt;

    public static StudentPickupPointOutput fromEntity(StudentPickupPoint entity) {
        return StudentPickupPointOutput.builder()
            .id(entity.getId())
            .studentId(entity.getStudent().getId())
            .pickupPointId(entity.getPickupPoint().getId())
            .status(entity.getStatus())
            .getInAt(entity.getGetInAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
