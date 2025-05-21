package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripTimelineDto {
    private UUID tripId;
    private String tripCode;
    private String currentStatus;
    private Instant startTime;
    private Instant endTime;
    private Integer currentStopIndex;
    private Integer totalStops;
    private List<TripTimelineEventDto> events;
}
