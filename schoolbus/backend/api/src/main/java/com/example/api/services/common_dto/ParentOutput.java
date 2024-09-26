package com.example.api.services.common_dto;

import com.example.shared.db.entities.Parent;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParentOutput {
    private Long id;
    private Long accountId;
    private String name;
    private String avatar;
    private Instant dob;
    private String phoneNumber;

    private Instant createdAt;
    private Instant updatedAt;

    public static ParentOutput fromEntity(Parent entity) {
        return ParentOutput.builder()
            .id(entity.getId())
            .accountId(entity.getAccount().getId())
            .name(entity.getName())
            .avatar(entity.getAvatar())
            .dob(entity.getDob())
            .phoneNumber(entity.getPhoneNumber())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
