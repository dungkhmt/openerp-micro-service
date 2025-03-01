package openerp.openerpresourceserver.examtimetabling.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableSession;

public interface ExamTimetableSessionRepository extends JpaRepository<ExamTimetableSession, UUID> {
    List<ExamTimetableSession> findByExamTimetableSessionCollectionId(UUID collectionId);
}
