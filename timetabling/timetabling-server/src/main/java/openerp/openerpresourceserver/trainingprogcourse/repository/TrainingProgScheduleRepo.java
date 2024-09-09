package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingProgScheduleRepo extends JpaRepository<TrainingProgSchedule, Long> {
    List<TrainingProgSchedule> findByProgramId(String programId);
}
