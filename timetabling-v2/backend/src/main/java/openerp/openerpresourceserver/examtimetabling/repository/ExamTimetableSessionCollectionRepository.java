package openerp.openerpresourceserver.examtimetabling.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableSessionCollection;

public interface ExamTimetableSessionCollectionRepository extends JpaRepository<ExamTimetableSessionCollection, UUID> {
}
