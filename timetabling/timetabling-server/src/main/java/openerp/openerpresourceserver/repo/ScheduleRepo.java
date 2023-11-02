package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    List<Schedule> getSchedulesByClassRoomAndStudyWeekAndWeekDay(String classRoom, String studyWeek, String weekDay);

}
