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
public class ParentUpdateInput {
    private Long id;

    private String name;

    private Instant dob;

    private String avatar;

    private String username;

    private String phoneNumber;

    private String password;

    private List<Long> studentIds;
}
