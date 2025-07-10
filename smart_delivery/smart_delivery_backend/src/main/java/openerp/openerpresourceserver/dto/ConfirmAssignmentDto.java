package openerp.openerpresourceserver.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ConfirmAssignmentDto {
    private UUID hubId;
    private List<AssignmentDetailDto> assignments;

    @Data
    public static class AssignmentDetailDto {
        private UUID orderId;
        private UUID employeeId;
        private String employeeName;
        private Integer sequenceNumber;
    }
}