package com.example.api.services.account.dto;


import com.example.shared.db.entities.Parent;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentSearchOutput {
    private Long id;

    private String name;

    private String avatar;

    private Instant dob;

    private String phoneNumber;

    private Instant createdAt;

    private Instant updatedAt;

    private List<StudentSearchOutput> students;

    public static ParentSearchOutput from(Parent dto) {
        return ParentSearchOutput.builder()
            .id(dto.getId())
            .name(dto.getName())
            .avatar(dto.getAvatar())
            .dob(dto.getDob() != null ? Instant.ofEpochMilli(dto.getDob().toEpochMilli()) : null)
            .phoneNumber(dto.getPhoneNumber())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .students(dto.getStudents().stream().map(StudentSearchOutput::from).toList())
            .build();
    }
}
