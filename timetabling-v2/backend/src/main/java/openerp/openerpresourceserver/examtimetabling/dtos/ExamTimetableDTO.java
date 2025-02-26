package openerp.openerpresourceserver.examtimetabling.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ExamTimetableDTO {
    private UUID id;
    private String name;
    private UUID examPlanId;
    private double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
