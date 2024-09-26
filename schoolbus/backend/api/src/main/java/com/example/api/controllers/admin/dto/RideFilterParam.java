package com.example.api.controllers.admin.dto;

import com.example.shared.enumeration.RideStatus;

public class RideFilterParam {
    private RideStatus status;
    private Long busId;
    private String startAt;
    private String endAt;
    private String startFrom;
}
