package com.example.shared.db.repo;

import com.example.shared.db.entities.StudentAssign;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAssignRepository extends JpaRepository<StudentAssign, Long> {
    Optional<StudentAssign> findByStudentId(Long studentId);
}
