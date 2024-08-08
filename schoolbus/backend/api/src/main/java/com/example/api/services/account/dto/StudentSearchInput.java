package com.example.api.services.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSearchInput {
    private Long id;

    private String name;

    private Long parentId;

    private String studentClass;

    private String phoneNumber;

    private Pageable pageable;
}
