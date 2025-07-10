package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStopDTO {
    private UUID id;
    private UUID hubId;
    private String hubName;
    private String address;
    private String city;
    private String ward;
    private Double latitude;
    private Double longitude;
    private Integer stopSequence;
    private String estimatedArrivalTime;
    private String status; // "PENDING", "CURRENT", "COMPLETED"
    private Integer orderCount;
}
