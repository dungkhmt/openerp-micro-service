package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatisticsDto {
    private UUID employeeId;
    private String employeeName;
    private String employeeRole; // "COLLECTOR" or "SHIPPER"
    private Integer totalAssignments;
    private Integer completedAssignments;
    private Integer failedAssignments;
    private Integer pendingAssignments;
    private Double successRate;
    private Double averageCompletionTime; // In minutes
    private Map<String, Integer> assignmentStatusCounts;
}