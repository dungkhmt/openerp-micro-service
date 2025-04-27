package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long>, JpaSpecificationExecutor<Absence> {
    @Query(value =
            """
                    SELECT ab FROM Absence ab
                    JOIN Employee e ON e.id = ab.employee.id AND e.email = :email
                    JOIN AbsenceType at ON ab.absenceType.id = at.id
                    WHERE ab.type = :type AND ab.status IN (:status) AND
                    (ab.startTime BETWEEN :startDate AND :endDate OR ab.endTime BETWEEN :startDate AND :endDate)
                    """)
    List<Absence> getAbsenceInDateRange(
            String email,
            int type,
            List<Integer> status,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
