package openerp.containertransport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import openerp.containertransport.entity.Trip;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShipmentModel implements Serializable {
    private long id;
    private String code;
    @JsonProperty("created_by_user_id")
    private String createdByUserId;
    @JsonProperty("status")
    private String status;
    private List<TripModel> tripList;
    private long createdAt;
    private long updatedAt;
}
