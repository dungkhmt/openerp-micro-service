package com.example.api.services.account.dto;


import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentDetailOutput {
    private Long id;
    private String name;
    private String avatar;
    private String phoneNumber;
    private String username;
    private Instant dob;

    private List<StudentSearchOutput> students;

    private Instant createdAt;
    private Instant updatedAt;
}
