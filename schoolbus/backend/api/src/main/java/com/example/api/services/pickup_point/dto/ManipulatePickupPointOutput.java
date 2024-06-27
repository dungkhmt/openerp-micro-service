package com.example.api.services.pickup_point.dto;

import com.example.api.services.common_dto.BusOutput;
import com.example.api.services.common_dto.EmployeeOutput;
import com.example.api.services.common_dto.PickupPointOutput;
import com.example.api.services.common_dto.RideOutput;
import com.example.api.services.common_dto.RidePickupPointOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.api.services.common_dto.StudentPickupPointOutput;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManipulatePickupPointOutput {
    private BusOutput bus;
    private EmployeeOutput driver;
    private EmployeeOutput driverMate;
    private RideOutput ride;
    private List<PickupPointWithStudent> pickupPointWithStudents;

    @Data
    @Builder
    public static class PickupPointWithStudent {
        private PickupPointOutput pickupPoint;
        private RidePickupPointOutput ridePickupPoint;
        private List<StudentWithPickupPoint> studentWithPickupPoints;

        @Data
        @Builder
        public static class StudentWithPickupPoint {
            private StudentOutput student;
            private StudentPickupPointOutput studentPickupPoint;
        }
    }
}
