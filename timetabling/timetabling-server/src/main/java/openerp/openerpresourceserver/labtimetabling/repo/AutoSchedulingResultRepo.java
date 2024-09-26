package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AutoSchedulingResultRepo extends JpaRepository<AutoSchedulingResult, UUID> {
    @Query(value="SELECT * FROM timetable_lab_auto_scheduling_result where submission_id = ?1", nativeQuery = true)

    List<AutoSchedulingResult> findAllBySubmission_id(UUID submission_id);
}
