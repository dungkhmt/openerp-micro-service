package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.TimePerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimePerformanceRepo extends JpaRepository<TimePerformance, Long> {
    List<TimePerformance> findAll();

    List<TimePerformance> getTimePerformancesByClassRoomAndSemester(String classRoom, String semester);

    List<TimePerformance> getTimePerformancesByClassRoomAndSemesterAndWeekDay(String classRoom, String semester, String weekDay);

    void deleteAll();

    void deleteAllByClassRoomAndSemester(String classRoom, String semester);

    void deleteById(Long id);
}
