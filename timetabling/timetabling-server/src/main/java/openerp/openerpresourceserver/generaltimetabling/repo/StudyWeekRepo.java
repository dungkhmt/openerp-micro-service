package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyWeekRepo extends JpaRepository<StudyWeek, Long> {
    @Query(value = "SELECT DISTINCT study_week FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getStudyWeek();
}
