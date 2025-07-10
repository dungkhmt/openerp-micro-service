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
public class TripHistoryEntryDto {
    private UUID id;
    private String status;
    private Instant timestamp;
    private String changedBy;
    private Integer currentStopIndex;
    private String currentStopName;
    private String currentStopAddress;
    private String notes;
}