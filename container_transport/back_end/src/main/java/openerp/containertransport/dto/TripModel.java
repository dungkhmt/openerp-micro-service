package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import openerp.containertransport.entity.Order;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripModel implements Serializable {
    private long id;
    private String code;
    private Long shipmentId;
    private Long truckId;
    private String driverName;
    private String truckCode;
    private List<TripItemModel> tripItemModelList;
    private List<Long> orderIds;
    private List<Order> orders;
    private String status;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    private long createdAt;
    private long updatedAt;
}
