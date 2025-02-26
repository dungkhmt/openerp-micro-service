package openerp.openerpresourceserver.examtimetabling.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetable;

@Repository
public interface ExamTimetableRepository extends JpaRepository<ExamTimetable, UUID> {
    List<ExamTimetable> findByExamPlanIdAndDeletedAtIsNull(UUID examPlanId);
}
