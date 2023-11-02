package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.WeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekDayRepo extends JpaRepository<WeekDay, Long> {
    @Query(value = "SELECT DISTINCT week_day FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getWeekDay();
}
