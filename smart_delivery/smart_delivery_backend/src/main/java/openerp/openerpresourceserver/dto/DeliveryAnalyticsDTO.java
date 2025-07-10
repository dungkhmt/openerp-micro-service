package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAnalyticsDTO {
    private UUID hubId;
    private String hubName;
    private Integer totalDeliveries;
    private Integer completedDeliveries;
    private Integer failedDeliveries;
    private Integer pendingDeliveries;
    private Double successRate;
    private Double averageDeliveryTime; // in minutes
    private Map<String, Integer> deliveryStatusCounts;
    private List<ShipperPerformanceDTO> shipperPerformance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipperPerformanceDTO {
        private UUID shipperId;
        private String shipperName;
        private Integer totalAssignments;
        private Integer completedDeliveries;
        private Integer failedDeliveries;
        private Double successRate;
        private Double averageDeliveryTime; // in minutes
    }
}