package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleScheduleDto {

    private UUID id;

    @NotNull(message = "Route Vehicle ID is required")
    private UUID routeVehicleId;

    // For display purposes
    private String vehiclePlateNumber;
    private String routeName;
    private UUID vehicleId;
    private UUID routeId;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Number of trips is required")
    @Min(value = 1, message = "Number of trips must be at least 1")
    private Integer numberOfTrips;

    private boolean isActive;
}