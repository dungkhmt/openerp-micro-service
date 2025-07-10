package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
public class TripHistoryResponseDto {
    private UUID tripId;
    private String tripCode;
    private UUID routeId;
    private String routeName;
    private String routeCode;
    private String currentStatus;
    private Instant createdAt;
    private Instant startTime;
    private Instant endTime;
    private Integer currentStopIndex;
    private Integer totalStops;
    private Long totalDurationMinutes;
    private Integer totalOrders;
    private Integer deliveredOrders;
    private String completionNotes;
    private List<TripHistoryEntryDto> historyEntries;
    private List<TripStopTimelineDto> stopTimeline;
}
