package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import openerp.containertransport.entity.Order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripModel implements Serializable {
    private long id;
    private String uid;
    private String code;
    private String shipmentId;
    private Long truckId;
    private String truckUid;
    private String driverName;
    private String truckCode;
    private List<TripItemModel> tripItemModelList;
    private List<Long> orderIds;
    private List<Order> orders;
    private List<OrderModel> ordersModel;
    private String status;
    @JsonProperty("total_distant")
    private BigDecimal totalDistant;
    @JsonProperty("total_time")
    private BigDecimal totalTime;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    private long createdAt;
    private long updatedAt;
    private String actor;
    private Long startTime;
}
