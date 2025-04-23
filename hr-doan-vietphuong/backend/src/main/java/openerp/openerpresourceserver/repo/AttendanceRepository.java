package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Attendance;
import openerp.openerpresourceserver.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

    List<Attendance> findByDate(Integer date);

    @Query("SELECT a FROM Attendance a WHERE a.id = :employeeId AND a.date = :date")
    List<Attendance> findByEmployeeIdAndDate(Integer employeeId, Integer date);

    @Query("SELECT a FROM Attendance a WHERE a.id = :employeeId ORDER BY a.time DESC")
    List<Attendance> findLastAttendanceByEmployeeId(Integer employeeId);
}
