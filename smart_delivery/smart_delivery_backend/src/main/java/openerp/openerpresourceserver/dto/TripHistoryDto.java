package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripHistoryDto {
    private UUID tripId;
    private String tripCode;
    private UUID routeId;
    private String routeName;
    private String routeCode;
    private LocalDate tripDate;
    private TripStatus status;

    // Time metrics
    private Instant scheduledStartTime;
    private Instant actualStartTime;
    private Instant completionTime;
    private Long durationMinutes;

    // Performance metrics
    private Integer totalStops;
    private Integer stopsCompleted;
    private Double distanceTraveled; // In kilometers

    // Order metrics
    private Integer totalOrders;
    private Integer ordersDelivered;
    private Integer ordersFailed;
    private Double deliverySuccessRate;

    // Vehicle info
    private UUID vehicleId;
    private String vehiclePlateNumber;
    private Double vehicleUtilizationPercentage;

    // Additional data
    private String completionNotes;
    private List<DelayEventDto> delayEvents;

    // Inner class for delay events
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DelayEventDto {
        private String type; // e.g., "TRAFFIC", "VEHICLE_ISSUE", "WEATHER"
        private Instant timestamp;
        private Integer durationMinutes;
        private String description;
    }
}