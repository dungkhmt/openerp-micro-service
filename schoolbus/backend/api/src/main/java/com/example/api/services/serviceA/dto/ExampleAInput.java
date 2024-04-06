package com.example.api.services.serviceA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExampleAInput {
    private String description;
    private Instant createdFrom;
    private Instant createdTo;

    private String createdBy;

    private Integer page;

    private Integer size;

}
