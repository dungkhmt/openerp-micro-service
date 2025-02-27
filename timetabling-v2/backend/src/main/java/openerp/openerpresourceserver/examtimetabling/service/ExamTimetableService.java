package openerp.openerpresourceserver.examtimetabling.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDTO;
import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetable;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableAssignmentRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableRepository;

@Service
@RequiredArgsConstructor
public class ExamTimetableService {
    private final ExamTimetableRepository examTimetableRepository;
    private final ExamClassRepository examClassRepository;
    private final ExamTimetableAssignmentRepository examTimetableAssignmentRepository;
    private final EntityManager entityManager;
    
    @Transactional
    public ExamTimetable createExamTimetable(ExamTimetable examTimetable) {
        // Generate UUID if not provided
        if (examTimetable.getId() == null) {
            examTimetable.setId(UUID.randomUUID());
        }
        
        // Set timestamps and save timetable
        LocalDateTime now = LocalDateTime.now();
        examTimetable.setCreatedAt(now);
        examTimetable.setUpdatedAt(now);
        ExamTimetable savedTimetable = examTimetableRepository.save(examTimetable);
        
        // Use native SQL for bulk insert
        String insertSql = "INSERT INTO exam_timetable_assignment " +
                          "(id, exam_timetable_id, exam_timtabling_class_id, created_at, updated_at) " +
                          "SELECT uuid_generate_v4(), :timetableId, id, :createdAt, :updatedAt " +
                          "FROM exam_timetabling_class " +
                          "WHERE exam_plan_id = :examPlanId";
        
        Query query = entityManager.createNativeQuery(insertSql);
        query.setParameter("timetableId", savedTimetable.getId());
        query.setParameter("examPlanId", examTimetable.getExamPlanId());
        query.setParameter("createdAt", now);
        query.setParameter("updatedAt", now);
        
        query.executeUpdate();
        
        return savedTimetable;
    }
    
    public List<ExamTimetableDTO> getAllTimetablesByExamPlanId(UUID examPlanId) {
        // Get total count of exam classes for this plan
        long totalClasses = examClassRepository.countByExamPlanId(examPlanId);
        
        // Get all timetables for this plan
        List<ExamTimetable> timetables = examTimetableRepository.findByExamPlanIdAndDeletedAtIsNull(examPlanId);
        
        // Map to DTOs with progress calculation
        return timetables.stream().map(timetable -> {
            ExamTimetableDTO dto = new ExamTimetableDTO();
            dto.setId(timetable.getId());
            dto.setName(timetable.getName());
            dto.setExamPlanId(timetable.getExamPlanId());
            dto.setCreatedAt(timetable.getCreatedAt());
            dto.setUpdatedAt(timetable.getUpdatedAt());
            
            // Calculate progress
            long completedAssignments = examTimetableAssignmentRepository
                .countByExamTimetableIdAndRoomIdIsNotNullAndExamSessionIdIsNotNull(timetable.getId());
            
            double progressPercentage = totalClasses > 0 
                ? ((double) completedAssignments / totalClasses) * 100 
                : 0.0;
            
            dto.setProgressPercentage(Math.round(progressPercentage * 100.0) / 100.0); // Round to 2 decimal places
            
            return dto;
        }).collect(Collectors.toList());
    }
}
