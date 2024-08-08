package com.example.api.controllers.admin.dto;

import com.example.api.services.ride.dto.UpdateRideInput;
import com.example.shared.enumeration.RideStatus;
import com.example.shared.utils.DateConvertUtil;
import java.util.List;

public class UpdateRideRequest {
    private Long id;
    private Long busId;
    private String startAt;
    private String endAt;
    private String startFrom;
    private RideStatus status;

    public UpdateRideInput toInput() {
        return UpdateRideInput.builder()
                .id(id)
                .busId(busId)
                .startAt(DateConvertUtil.convertStringToInstant(startAt))
                .endAt(DateConvertUtil.convertStringToInstant(endAt))
                .status(status)
                .startFrom(startFrom)
                .build();
    }
}
