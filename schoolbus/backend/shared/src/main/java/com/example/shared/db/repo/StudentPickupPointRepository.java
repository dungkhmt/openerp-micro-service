package com.example.shared.db.repo;

import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Student;
import com.example.shared.db.entities.StudentPickupPoint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPickupPointRepository extends JpaRepository<StudentPickupPoint, Long> {
    void deleteAllByPickupPoint(PickupPoint pickupPoint);

    boolean existsByPickupPointId(Long pickupPointId);

    void deleteByStudent(Student student);

    void deleteByStudentId(Long studentId);

    Optional<StudentPickupPoint> findByStudentIdAndPickupPointId(Long studentId, Long pickupPointId);
    List<StudentPickupPoint> findByPickupPointIdIn(List<Long> pickupPointIds);
}
