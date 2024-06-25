package com.example.api.services.common_dto;

import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.Student;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentOutput {
    private Long id;
    private String name;
    private String avatar;
    private Instant dob;
    private String studentClass;
    private String phoneNumber;
    private Long parentId;
    private Instant createdAt;
    private Instant updatedAt;

    private String numberPlateAssign;

    public static StudentOutput fromEntity(Student entity) {
        return StudentOutput.builder()
            .id(entity.getId())
            .name(entity.getName())
            .avatar(entity.getAvatar())
            .dob(entity.getDob())
            .studentClass(entity.getStudentClass())
            .phoneNumber(entity.getPhoneNumber())
            .parentId(entity.getParent().getId())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
