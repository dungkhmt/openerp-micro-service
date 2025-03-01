package openerp.openerpresourceserver.examtimetabling.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ExamTimetableDetailDTO {
    private UUID id;
    private String name;
    private UUID examPlanId;
    private Integer planStartWeek;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private UUID sessionCollectionId;
    private String sessionCollectionName;
    private List<WeekDTO> weeks;
    private List<DateDTO> dates;
    private List<SlotDTO> slots;
    private List<AssignmentDTO> assignments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class WeekDTO {
        private String id;
        private String name;
    }
    
    @Data
    public static class DateDTO {
        private String id;
        private String weekId;
        private String name;
        private LocalDate date;
    }
    
    @Data
    public static class SlotDTO {
        private String id;
        private UUID originalId;
        private String name;
    }
    
    @Data
    public static class AssignmentDTO {
        private UUID id;
        private UUID examClassId;
        private String examClassIdentifier;
        private String classId;
        private String courseId;
        private String groupId;
        private String courseName;
        private String description;
        private Integer numberOfStudents;
        private String period;
        private String managementCode;
        private String school;
        private String roomId;
        private UUID sessionId;
        private String sessionName;
        private String slotId;  // Add this field
        private Integer weekNumber;
        private LocalDate date;
    }
}
