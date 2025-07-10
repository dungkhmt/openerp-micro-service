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
public class DriverStatisticsDto {
    private UUID driverId;
    private String driverName;
    private String driverCode;
    private String driverPhone;
    private String username;

    // Overall metrics
    private Integer totalTrips;
    private Integer completedTrips;
    private Integer cancelledTrips;
    private Double completionRate;

    // Performance metrics
    private Double averageTripDuration; // In minutes
    private Integer totalOrdersDelivered;
    private Integer totalOrdersFailed;
    private Double deliverySuccessRate;

    // Vehicle utilization metrics
    private Double averageVehicleUtilization; // Percentage of capacity used
    private Double averageVehicleWeightUtilization;
    private Double averageVehicleVolumeUtilization;

    // Route efficiency metrics
    private Double averageTripDistance; // In kilometers
    private Double averageStopsPerTrip;
    private Double averageTimePerStop; // In minutes

    // Aggregated counts by status
    private Map<String, Integer> tripStatusCounts;
    private Map<String, Integer> orderStatusCounts;

    // Time efficiency metrics
    private Double averageTimeToStart; // Average time to start trip after scheduled time
    private Double onTimeStartPercentage; // Percentage of trips started on time
    private Double onTimeCompletionPercentage; // Percentage of trips completed on time
}