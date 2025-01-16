package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingProgPrerequisiteRepo extends JpaRepository<TrainingProgPrerequisite, TrainingProgPrerequisiteId> {
    @Query("SELECT s.course FROM TrainingProgSchedule s WHERE s.program.id = :programId")
    List<TrainingProgCourse> findCoursesByProgramId(@Param("programId") String programId);
}
