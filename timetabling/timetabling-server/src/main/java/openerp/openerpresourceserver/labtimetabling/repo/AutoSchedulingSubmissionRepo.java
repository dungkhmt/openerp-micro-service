package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingResult;
import openerp.openerpresourceserver.labtimetabling.entity.autoscheduling.AutoSchedulingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AutoSchedulingSubmissionRepo extends JpaRepository<AutoSchedulingSubmission, UUID> {
    @Query(value="SELECT * FROM timetable_lab_auto_scheduling_submission where semester_id = ?1", nativeQuery = true)

    List<AutoSchedulingSubmission> findAllBySemester_id(Long semester_id);
}
