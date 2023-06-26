package openerp.containertransport.algorithms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Point implements Serializable {
    public Long id;
    public int facilityId;
    public BigDecimal longitude;
    public BigDecimal latitude;
    public int seq;
    public String action;
    @JsonProperty("arrival_time")
    public Long arrivalTime;
    @JsonProperty("departure_time")
    public Long departureTime;
    @JsonProperty("container_id")
    public Long containerId;
    public Integer weightContainer;
    public Long totalTime;
    public Long latePickupContainer;
    public Long lateDeliveryContainer;
    @JsonProperty("trailer_id")
    public Integer trailerId;

    @JsonProperty("order_code")
    public String orderCode;

    public String type;
    public Boolean isBreakRomooc;
    public int prevPoint;
    public int nextPoint;
    public int nbTrailer;
    public int sizeContainer;
    public BigDecimal totalPrevCost;
    public BigDecimal totalPrevTime;

}
