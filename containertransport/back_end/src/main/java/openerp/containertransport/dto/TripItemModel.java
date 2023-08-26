package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripItemModel implements Serializable {
    private long id;
    private String uid;
    private String code;
    private String tripId;
    private long seq;
    private String action;
    private Long facilityId;
    private String facilityName;
    private String facilityCode;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long containerId;
    private String containerCode;
    private Long trailerId;
    private String trailerCode;
    private Long truckId;
    private String orderCode;
    private String orderUid;
    @JsonProperty("status")
    private String status;
    private String type;
    private long arrivalTime;
    private long departureTime;
    private long createdAt;
    private long updatedAt;
    private String typeOrder;
    private long lateArrivalTime;
    private long lateDepartureTime;
    private ContainerModel container;
    private long lateTime;
}
