package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

    List<Schedule> getSchedulesByClassRoomAndStudyWeekAndWeekDay(String classRoom, String studyWeek, String weekDay);

    @Query(value = "SELECT DISTINCT semester FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getSemester();

    @Query(value = "SELECT DISTINCT institute FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getInstitute();

    @Query(value = "SELECT DISTINCT tb.class_code, tb.bundle_class_code " +
            "FROM public.timetabling_schedule tb", nativeQuery = true)
    List<String> getClassCode();

    @Query(value = "SELECT DISTINCT module_code, module_name, module_name_by_english, mass " +
            "FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getModule();

    @Query(value = "SELECT DISTINCT study_time, start, finish, crew" +
            "FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getStudyTime();

    @Query(value = "SELECT DISTINCT study_week FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getStudyWeek();

    @Query(value = "SELECT DISTINCT class_room FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getClassroom();

    @Query(value = "SELECT DISTINCT week_day FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getWeekDay();

    @Query(value = "SELECT DISTINCT state FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getState();

    @Query(value = "SELECT DISTINCT class_type FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getClassType();

    @Query(value = "SELECT DISTINCT open_batch FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getOpenBatch();

    @Query(value = "SELECT DISTINCT management_code FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getManagementCode();
}
