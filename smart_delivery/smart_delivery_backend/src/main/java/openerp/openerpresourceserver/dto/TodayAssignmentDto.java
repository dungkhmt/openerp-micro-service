package openerp.openerpresourceserver.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TodayAssignmentDto {
    private UUID collectorId;
    private String collectorName;
    private Long numOfOrders;
    private Long numOfCompleted;
    private String collectorPhone;
    private String status;
}
