package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyTimeRepo extends JpaRepository<StudyTime, Long> {
    @Query(value = "SELECT DISTINCT study_time, start, finish, crew " +
            "FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getStudyTime();
}
