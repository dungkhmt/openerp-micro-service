package com.example.api.services.history.dto;

import com.example.shared.enumeration.RideStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
public class AdminHistoryRideFilterParam {
    // filter for ride
    private String startAt;
    private Integer rideId;
    private String numberPlate;
    private RideStatus status;
    private Boolean isToSchool;
    private String address;

    // filter for student
    private String studentPhoneNumber;
    private String parentPhoneNumber;

    private Integer page;
    private Integer size;
    private String sort;
}
