package com.example.api.controllers.client.dto;

import lombok.Data;

@Data
public class StudentAddress {
    private Long studentId;
    private String address;
    private Double longitude;
    private Double latitude;
}
