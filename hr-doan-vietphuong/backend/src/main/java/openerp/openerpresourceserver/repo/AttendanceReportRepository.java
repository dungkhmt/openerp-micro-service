package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long>, JpaSpecificationExecutor<AttendanceReport> {
    List<AttendanceReport> findByEmployeeIdAndDateBetween(int employeeId, int startDate, int endDate);

    Optional<AttendanceReport> findByEmployeeId(int employeeId);

    Optional<AttendanceReport> findByEmployeeIdAndDate(Integer employeeId, int dateInteger);

    List<AttendanceReport> findByDateBetween(int startDate, int endDate);
}
