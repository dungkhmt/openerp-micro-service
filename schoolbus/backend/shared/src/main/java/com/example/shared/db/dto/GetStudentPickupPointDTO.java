package com.example.shared.db.dto;

import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.Student;
import java.util.List;

public interface GetStudentPickupPointDTO {
    PickupPoint getPickupPoint();
    Student getStudent();
}
