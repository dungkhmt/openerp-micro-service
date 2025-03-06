package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteDTO {
    private UUID shipperId;
    private String shipperName;
    private List<DeliveryStopDTO> stops;
    private Double totalDistance;
    private Integer estimatedDuration; // in minutes
    private String startingHub;
    private UUID hubId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryStopDTO {
        private UUID orderId;
        private int sequenceNumber;
        private String recipientName;
        private String recipientAddress;
        private String recipientPhone;
        private Double recipientLatitude;
        private Double recipientLongitude;
        private Double distanceFromPreviousStop;
        private String estimatedDeliveryTime;
        private String notes;
    }
}