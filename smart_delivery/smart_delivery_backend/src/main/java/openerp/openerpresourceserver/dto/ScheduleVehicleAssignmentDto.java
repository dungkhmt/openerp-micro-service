package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.RouteSchedule;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVehicleAssignmentDto {

    private UUID id;
    @NotNull(message = "Route schedule ID is required")
    private UUID routeScheduleId;
    private RouteScheduleDto routeScheduleDto;
    private RouteDto routeDto;
    private String routeName;
    private String routeCode;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    private String vehiclePlateNumber;

    private UUID driverId;

    private String driverName;

    private LocalDate assignmentDate;

    private boolean isActive;

    private Instant assignedAt;

    private Instant unassignedAt;
}