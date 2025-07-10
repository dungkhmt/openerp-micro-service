package openerp.openerpresourceserver.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TodayAssignmentShipperDto {
    private UUID shipperId;
    private String shipperName;
    private Long numOfOrders;
    private Long numOfCompleted;
    private String shipperPhone;
    private String status;
}
