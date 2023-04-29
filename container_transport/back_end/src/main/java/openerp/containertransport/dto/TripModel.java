package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    private Integer truckId;
    private String truckName;
    private List<TripItemModel> tripItemModelList;
    private String status;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    private long createdAt;
    private long updatedAt;
}
