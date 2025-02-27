package openerp.openerpresourceserver.examtimetabling.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableAssignment;

@Repository
public interface ExamTimetableAssignmentRepository extends JpaRepository<ExamTimetableAssignment, UUID> {
    long countByExamTimetableIdAndRoomIdIsNotNullAndExamSessionIdIsNotNull(UUID examTimetableId);
}
