package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingProgCourseRepo extends JpaRepository<TrainingProgCourse, String> {
}
