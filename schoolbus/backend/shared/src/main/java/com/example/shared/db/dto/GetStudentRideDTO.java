package com.example.shared.db.dto;

import com.example.shared.db.entities.Bus;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.RidePickupPoint;
import com.example.shared.db.entities.Student;
import java.util.List;

public interface GetStudentRideDTO {
    Student getStudent();
    PickupPoint getPickupPoint();
    List<ExecutionDTO> getExecutions();


    interface ExecutionDTO {
        Ride getRide();
        Bus getBus();
        List<PickupPoint> getPickupPoints();
        List<RidePickupPoint> getRidePickupPoints();
    }

}
