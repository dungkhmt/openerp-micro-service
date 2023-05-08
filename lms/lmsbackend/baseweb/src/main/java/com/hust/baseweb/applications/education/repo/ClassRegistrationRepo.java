package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.ClassRegistration;
import com.hust.baseweb.applications.education.entity.ClassRegistrationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ClassRegistrationRepo extends JpaRepository<ClassRegistration, ClassRegistrationId> {

    @Query(value = "select status \n" +
                   "from edu_class_registration \n" +
                   "where class_id = ?1 and student_id = ?2",
           nativeQuery = true)
    String checkRegistration(UUID classId, String studentId);

}
