package com.example.api.services.serviceA.dto;

import com.example.shared.db.entities.Example;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleAOutput {
    private Long id;
    private String description;
    private Instant createdAt;

    private String createdBy;

    private Integer page;

    private Integer size;

    public static ExampleAOutput fromExample(Example example) {
        return ExampleAOutput.builder()
                .id(example.getId())
                .description(example.getDescription())
                .createdAt(example.getCreatedAt())
                .createdBy(example.getCreatedBy())
                .build();
    }
}
