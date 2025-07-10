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
public class TripStopTimelineDto {
    private Integer stopIndex;
    private Integer stopSequence;
    private UUID hubId;
    private String hubName;
    private String address;
    private String status;  // COMPLETED, CURRENT, PENDING
    private Instant arrivalTime;
    private Integer orderCount;
}