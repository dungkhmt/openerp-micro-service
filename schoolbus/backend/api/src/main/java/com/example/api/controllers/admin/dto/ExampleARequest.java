package com.example.api.controllers.admin.dto;

import com.example.api.services.serviceA.dto.ExampleAInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleARequest {
    private String description;
    private Instant createdFrom;
    private Instant createdTo;
    private String createdBy;
    private Integer page;
    private Integer size;
    public ExampleAInput toInput() {
        return ExampleAInput.builder()
                .description(description)
                .createdFrom(createdFrom)
                .createdTo(createdTo)
                .createdBy(createdBy)
                .build();
    }
}
