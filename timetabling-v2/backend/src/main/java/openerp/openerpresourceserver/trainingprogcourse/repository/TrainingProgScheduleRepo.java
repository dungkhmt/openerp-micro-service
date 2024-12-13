package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgScheduleRepo extends JpaRepository<TrainingProgSchedule, Long> {
    List<TrainingProgSchedule> findByProgramId(String programId);


    @Query("SELECT tps FROM TrainingProgSchedule tps " +
            "WHERE tps.program.id = :programId AND tps.course.id = :courseId")
    Optional<TrainingProgSchedule> findByProgramIdAndCourseId(
            @Param("programId") String programId,
            @Param("courseId") String courseId
    );

}
