package com.example.api.services.history.dto;


import com.example.api.services.common_dto.BusOutput;
import com.example.api.services.common_dto.EmployeeOutput;
import com.example.api.services.common_dto.RideOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.shared.db.entities.RideHistory;
import com.example.shared.db.entities.RidePickupPointHistory;
import com.example.shared.db.entities.StudentPickupPointHistory;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeHistoryRideOutput {
    private BusOutput bus;
    private RideOutput ride;
    private EmployeeOutput driver;
    private EmployeeOutput driverMate;
    private List<RideHistory> rideHistories;
    private List<RidePickupPointHistory> ridePickupPointHistories;
    private List<StudentRideHistory> studentRideHistories;

    @Data
    @Builder
    public static class StudentRideHistory {
        private StudentOutput student;
        private List<StudentPickupPointHistory> studentPickupPointHistories;
    }
}
