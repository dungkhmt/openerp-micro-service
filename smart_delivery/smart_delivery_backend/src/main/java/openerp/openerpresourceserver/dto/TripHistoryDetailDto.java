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
public class TripHistoryDetailDto {
    private UUID id;
    private UUID tripId;
    private String status;
    private String nextStatus;
    private Instant createdAt;
    private String changedBy;
    private Integer currentStopIndex;
    private String stopHubName;
    private String stopAddress;
    private Long durationToNextStatus;
    private String contextInfo;
}
