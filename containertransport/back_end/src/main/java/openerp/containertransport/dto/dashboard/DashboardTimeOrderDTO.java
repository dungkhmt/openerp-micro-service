package openerp.containertransport.dto.dashboard;

import lombok.Data;

@Data
public class DashboardTimeOrderDTO {
    private long startTime;
    private long endTime;
    private long countNewOrder;
    private long countDoneOrder;
}
