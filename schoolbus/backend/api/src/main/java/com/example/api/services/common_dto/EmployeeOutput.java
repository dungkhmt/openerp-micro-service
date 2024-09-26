package com.example.api.services.common_dto;

import com.example.shared.db.entities.Employee;
import com.example.shared.enumeration.EmployeeRole;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeOutput {
    private Long id;
    private Long accountId;
    private String name;
    private String avatar;
    private Instant dob;
    private String phoneNumber;
    private Long busId;
    private EmployeeRole role;

    private Instant createdAt;
    private Instant updatedAt;

    public static EmployeeOutput fromEntity(Employee entity) {
        return EmployeeOutput.builder()
            .id(entity.getId())
            .accountId(entity.getAccount().getId())
            .name(entity.getName())
            .avatar(entity.getAvatar())
            .dob(entity.getDob())
            .phoneNumber(entity.getPhoneNumber())
            .busId(entity.getBusId())
            .role(entity.getRole())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
