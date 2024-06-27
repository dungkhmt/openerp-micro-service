package com.example.api.services.account.dto;


import com.example.shared.db.entities.Student;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetailOutput {
    private Long id;

    private String name;

    private String avatar;

    private Instant dob;

    private String phoneNumber;

    private String studentClass;

    private Long parentId;

    private String parentName;

    private String parentPhoneNumber;

    private Instant createdAt;

    private Instant updatedAt;


    public static StudentDetailOutput from(Student dto) {
        return StudentDetailOutput.builder()
            .id(dto.getId())
            .name(dto.getName())
            .avatar(dto.getAvatar())
            .dob(dto.getDob() != null ? Instant.ofEpochMilli(dto.getDob().toEpochMilli()) : null)
            .phoneNumber(dto.getPhoneNumber())
            .studentClass(dto.getStudentClass())
            .parentId(dto.getParent() != null ? dto.getParent().getId() : null)
            .parentName(dto.getParent() != null ? dto.getParent().getName() : null)
            .parentPhoneNumber(dto.getParent() != null ? dto.getParent().getPhoneNumber() : null)
            .createdAt(dto.getCreatedAt() != null ?
                Instant.ofEpochMilli(dto.getCreatedAt().toEpochMilli()) : null)
            .updatedAt(dto.getUpdatedAt() != null ?
                Instant.ofEpochMilli(dto.getUpdatedAt().toEpochMilli()) : null)
            .build();
    }
}
