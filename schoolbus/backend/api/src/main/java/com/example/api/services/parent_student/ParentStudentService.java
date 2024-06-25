package com.example.api.services.parent_student;

import com.example.api.services.parent_student.dto.GetStudentRideOutput;
import com.example.shared.db.entities.Account;
import java.util.List;

public interface ParentStudentService {
    List<GetStudentRideOutput> getStudentRides(Account account);

    List<GetStudentRideOutput> getStudentRides(List<Long> studentIds);
}
