package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripTimelineEventDto {
    private String status;
    private Instant timestamp;
    private String title;
    private String description;
    private String hubName;
    private boolean completed;
}