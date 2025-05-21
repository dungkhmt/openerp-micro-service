package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDetailsDTO {
    private UUID id;
    private UUID routeId;
    private String routeName;
    private TripStatus status;
    private Instant startTime;
    private UUID routeScheduleId;
    private Instant endTime;
    private Integer currentStopIndex;
    private Integer totalStops;
    private Integer ordersCount;
    private Integer ordersDelivered;
    private List<TripStopDTO> stops;
    private List<OrderSummaryDTO> orders;
    private List<TripStatusChangeDTO> statusHistory;


}