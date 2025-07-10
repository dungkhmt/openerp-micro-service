package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class RouteScheduleRequestDto {
    private UUID routeId;
    private String routeCode;
    private String routeName;
    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;
    private int numberOfTrips;
    private boolean isActive;
}
