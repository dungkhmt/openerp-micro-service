package com.example.api.services.common_dto;

import com.example.shared.db.entities.RequestRegistration;
import com.example.shared.enumeration.RequestRegistrationStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRegistrationOutput {
    private Long id;
    private Long parentId;
    private Long studentId;
    private RequestRegistrationStatus status;
    private String address;
    private Double longitude;
    private Double latitude;
    private String note;

    private Instant createdAt;
    private Instant updatedAt;

    public static RequestRegistrationOutput fromEntity(RequestRegistration entity) {
        return RequestRegistrationOutput.builder()
            .id(entity.getId())
            .parentId(entity.getParent().getId())
            .studentId(entity.getStudent().getId())
            .status(entity.getStatus())
            .address(entity.getAddress())
            .longitude(entity.getLongitude())
            .latitude(entity.getLatitude())
            .note(entity.getNote())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
