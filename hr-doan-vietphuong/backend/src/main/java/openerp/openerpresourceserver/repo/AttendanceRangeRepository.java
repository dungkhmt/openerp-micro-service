package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttendanceRangeRepository extends JpaRepository<AttendanceRange, Long>,
        JpaSpecificationExecutor<AttendanceRange> {

    boolean existsByCode(String code);

    boolean existsByIdNotAndCode(long id, String code);
    @Query(value = """
            SELECT ar FROM AttendanceRange ar
            JOIN Employee e ON e.attendanceRange.id = ar.id AND e.email = :userEmail
            """)
    AttendanceRange findByEmployeeEmail(String userEmail);
}
