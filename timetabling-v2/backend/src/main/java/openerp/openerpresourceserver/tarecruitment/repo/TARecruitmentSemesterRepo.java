package openerp.openerpresourceserver.tarecruitment.repo;

import openerp.openerpresourceserver.tarecruitment.entity.TASemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TARecruitmentSemesterRepo extends JpaRepository<TASemester, String> {
    @Query("SELECT s.id FROM TASemester s ORDER BY s.id DESC")
    List<String> getAllSemester();

    @Query("SELECT s.id FROM TASemester s WHERE :currentDate BETWEEN s.startTime AND s.endTime")
    String getCurrentSemester(Date currentDate);
}
