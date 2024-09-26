package com.example.shared.db.repo;

import com.example.shared.db.entities.StudentPickupPointHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPickupPointHistoryRepository extends JpaRepository<StudentPickupPointHistory, Long>{
    List<StudentPickupPointHistory> findByRideId(Long rideId);
    List<StudentPickupPointHistory> findByRideIdAndStudentId(Long rideId, Long studentId);
}
