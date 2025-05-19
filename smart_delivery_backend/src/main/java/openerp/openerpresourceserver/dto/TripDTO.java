package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private UUID id;
    private UUID routeScheduleId;
    private String routeName;
    private String routeCode;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private TripStatus status; // "PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    private Instant startTime;
    private Instant endTime;
    private Integer currentStopIndex;
    private Integer totalStops;
    private Integer ordersCount;
    private Integer packagesCount;
    private Integer ordersDelivered;
}
