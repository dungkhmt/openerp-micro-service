package openerp.openerpresourceserver.examtimetabling.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDetailDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDetailDTO.AssignmentDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDetailDTO.DateDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDetailDTO.SlotDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDetailDTO.WeekDTO;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetable;
import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableSession;
import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetableSessionCollection;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamPlanRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableAssignmentRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableSessionCollectionRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamTimetableSessionRepository;

@Service
@RequiredArgsConstructor
public class ExamTimetableService {
    private final ExamTimetableRepository examTimetableRepository;
    private final ExamClassRepository examClassRepository;
    private final ExamTimetableAssignmentRepository examTimetableAssignmentRepository;
    private final EntityManager entityManager;
    private final ExamPlanRepository examPlanRepository;
    private final ExamTimetableAssignmentRepository assignmentRepository;
    private final ExamTimetableSessionCollectionRepository sessionCollectionRepository;
    private final ExamTimetableSessionRepository sessionRepository;
    
    public ExamTimetableDetailDTO getTimetableDetail(UUID timetableId) {
        // Get timetable
        ExamTimetable timetable = examTimetableRepository.findById(timetableId)
            .orElseThrow(() -> new RuntimeException("Timetable not found"));
            
        // Get exam plan
        ExamPlan plan = examPlanRepository.findById(timetable.getExamPlanId())
            .orElseThrow(() -> new RuntimeException("Exam plan not found"));
            
        // Get session collection and sessions
        ExamTimetableSessionCollection collection = sessionCollectionRepository
            .findById(timetable.getExamTimetableSessionCollectionId())
            .orElseThrow(() -> new RuntimeException("Session collection not found"));
            
        List<ExamTimetableSession> sessions = sessionRepository
            .findByExamTimetableSessionCollectionId(collection.getId());
            
        // Get assignments with exam class details
        List<Object[]> assignmentsWithDetails = assignmentRepository
            .findAssignmentsWithDetailsByTimetableId(timetableId);
            
        // Build response DTO
        ExamTimetableDetailDTO detailDTO = new ExamTimetableDetailDTO();
        detailDTO.setId(timetable.getId());
        detailDTO.setName(timetable.getName());
        detailDTO.setExamPlanId(plan.getId());
        detailDTO.setPlanStartWeek(plan.getStartWeek());
        detailDTO.setPlanStartTime(plan.getStartTime());
        detailDTO.setPlanEndTime(plan.getEndTime());
        detailDTO.setSessionCollectionId(collection.getId());
        detailDTO.setSessionCollectionName(collection.getName());
        detailDTO.setCreatedAt(timetable.getCreatedAt());
        detailDTO.setUpdatedAt(timetable.getUpdatedAt());
        
        // Generate time structure (weeks, dates, slots)
        TimeStructureDTO timeStructure = generateTimeStructure(
            plan.getStartWeek(), 
            plan.getStartTime().toLocalDate(), 
            plan.getEndTime().toLocalDate(),
            sessions
        );
        
        detailDTO.setWeeks(timeStructure.getWeeks());
        detailDTO.setDates(timeStructure.getDates());
        detailDTO.setSlots(timeStructure.getSlots());
        
        // Map assignments using the session-to-slot mapping
        Map<UUID, String> sessionToSlotMap = timeStructure.getSessionToSlotMap();
        
        List<AssignmentDTO> assignmentDTOs = mapAssignmentsWithSlots(
            assignmentsWithDetails, 
            sessionToSlotMap
        );
        
        detailDTO.setAssignments(assignmentDTOs);
        
        return detailDTO;
    }
    
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

        
    @Data
    private static class TimeStructureDTO {
        private List<WeekDTO> weeks;
        private List<DateDTO> dates;
        private List<SlotDTO> slots;
        private Map<UUID, String> sessionToSlotMap;
    }
    
    private TimeStructureDTO generateTimeStructure(
            Integer startWeek, 
            LocalDate startDate, 
            LocalDate endDate,
            List<ExamTimetableSession> sessions) {
        
        TimeStructureDTO result = new TimeStructureDTO();
        
        // Calculate the start of the week containing the startDate
        // In Vietnam, weeks typically start on Monday
        LocalDate firstDayOfStartWeek = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        
        // Generate weeks
        List<WeekDTO> weeks = new ArrayList<>();
        LocalDate currentWeekStart = firstDayOfStartWeek;
        int currentWeek = startWeek;
        
        while (!currentWeekStart.isAfter(endDate)) {
            WeekDTO week = new WeekDTO();
            week.setId("W" + currentWeek);
            week.setName("T" + currentWeek);
            weeks.add(week);
            
            // Move to next week
            currentWeekStart = currentWeekStart.plusWeeks(1);
            currentWeek++;
        }
        
        // Generate dates
        List<DateDTO> dates = new ArrayList<>();
        LocalDate currentDate = startDate; // Start from the actual start date, not beginning of week
        
        while (!currentDate.isAfter(endDate)) {
            // Skip weekends if needed (uncomment if you want to exclude weekends)
            // if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && 
            //     currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                
            DateDTO dateDTO = new DateDTO();
            
            // Calculate which week this date belongs to
            LocalDate mondayOfThisWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            int weeksDiff = (int) ChronoUnit.WEEKS.between(firstDayOfStartWeek, mondayOfThisWeek);
            int weekNumber = startWeek + weeksDiff;
            
            String dayOfWeek;
            switch (currentDate.getDayOfWeek()) {
                case MONDAY: dayOfWeek = "T2"; break;
                case TUESDAY: dayOfWeek = "T3"; break;
                case WEDNESDAY: dayOfWeek = "T4"; break;
                case THURSDAY: dayOfWeek = "T5"; break;
                case FRIDAY: dayOfWeek = "T6"; break;
                case SATURDAY: dayOfWeek = "T7"; break;
                case SUNDAY: dayOfWeek = "CN"; break;
                default: dayOfWeek = ""; break;
            }
            
            String dateStr = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            dateDTO.setId("D" + currentDate.format(DateTimeFormatter.ofPattern("ddMM")));
            dateDTO.setWeekId("W" + weekNumber);
            dateDTO.setName(dayOfWeek + " (" + dateStr + ")");
            dateDTO.setDate(currentDate);
            
            dates.add(dateDTO);
            // }
            
            // Move to next day
            currentDate = currentDate.plusDays(1);
        }
        
        // Map sessions to slots
        List<SlotDTO> slots = sessions.stream().map(session -> {
            SlotDTO dto = new SlotDTO();
            String sessionId = "S" + session.getName().replaceAll("[^0-9]", ""); // Extract numeric part
            dto.setId(sessionId);
            dto.setOriginalId(session.getId());
            
            // Create display name
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
            String startTimeStr = session.getStartTime().format(timeFormatter);
            String endTimeStr = session.getEndTime().format(timeFormatter);
            dto.setName(session.getName() + " (" + startTimeStr + " - " + endTimeStr + ")");
            
            return dto;
        }).collect(Collectors.toList());
        
        // Create mapping from original session IDs to slot IDs
        Map<UUID, String> sessionToSlotMap = slots.stream()
            .collect(Collectors.toMap(SlotDTO::getOriginalId, SlotDTO::getId));
        
        result.setWeeks(weeks);
        result.setDates(dates);
        result.setSlots(slots);
        result.setSessionToSlotMap(sessionToSlotMap);
        
        return result;
    }
    
    private List<AssignmentDTO> mapAssignmentsWithSlots(
            List<Object[]> assignmentsWithDetails,
            Map<UUID, String> sessionToSlotMap) {
            
        return assignmentsWithDetails.stream().map(row -> {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setId((UUID) row[0]);
            dto.setExamClassId((UUID) row[1]);
            dto.setExamClassIdentifier((String) row[2]);
            // Map other fields...
            
            UUID sessionId = (UUID) row[13];
            dto.setSessionId(sessionId);
            
            if (sessionId != null && sessionToSlotMap.containsKey(sessionId)) {
                dto.setSlotId(sessionToSlotMap.get(sessionId));
            }
            
            dto.setWeekNumber((Integer) row[14]);
            dto.setDate((LocalDate) row[15]);
            
            return dto;
        }).collect(Collectors.toList());
    }
}
