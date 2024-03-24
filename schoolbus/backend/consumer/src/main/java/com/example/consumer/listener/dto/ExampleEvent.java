package com.example.consumer.listener.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class ExampleEvent {
    private Long id;
    private String description;
    private Instant createdAt;
    private String createdBy;
}