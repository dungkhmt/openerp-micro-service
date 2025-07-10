package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSummaryDTO {
    private UUID tripId;
    private Instant startTime;
    private Instant endTime;
    private Long durationMinutes;
    private Integer totalStops;
    private Integer ordersDelivered;
    private Integer ordersFailed;
    private String completionNotes;
}