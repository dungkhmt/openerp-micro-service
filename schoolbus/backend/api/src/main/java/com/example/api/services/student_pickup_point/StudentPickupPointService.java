package com.example.api.services.student_pickup_point;

import com.example.api.services.student_pickup_point.dto.UpdateStudentPickupPointEmployeeInput;
import com.example.shared.db.entities.Account;

public interface StudentPickupPointService {
    void updateStudentPickupPointEmployee(UpdateStudentPickupPointEmployeeInput input,
                                                 Account account);
}
