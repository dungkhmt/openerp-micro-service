package com.example.api.services.bus.dto;

import com.example.shared.enumeration.BusStatus;
import java.util.List;
import lombok.Data;

@Data
public class ListBusFilterParam {
    private String numberPlate;
    private Integer seatNumber;
    private List<BusStatus> statuses;
    private String driverName;
    private Long driverId;
    private String driverMateName;
    private Long driverMateId;
}
