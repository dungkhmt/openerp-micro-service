package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripItemModel implements Serializable {
    private long id;
    private String code;
    private Long tripId;
    private long seqInTrip;
    private String action;
    private Long facilityId;
    private String facilityName;
    private Long containerId;
    private String containerCode;
    private Long trailerId;
    private String trailerCode;
    private String orderId;
    @JsonProperty("status")
    private String status;
    private long arrivalTime;
    private long departureTime;
    private long createdAt;
    private long updatedAt;
}
