package com.example.api.services.account.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentUpdateInput {
    private Long id;

    private String name;

    private String studentClass;

    private Long parentId;

    private Instant dob;

    private String avatar;

    private String phoneNumber;

}
