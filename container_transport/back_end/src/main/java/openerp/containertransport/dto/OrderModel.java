package openerp.containertransport.dto;

import lombok.*;
import openerp.containertransport.entity.Container;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderModel implements Serializable {
    private long id;
    private String orderCode;
    private String type;
    private long weight;
    private boolean isBreakRomooc;
    private String customerId;
    private List<Integer> containerIds;
    private Integer fromFacilityId;
    private String fromFacilityName;
    private String fromFacilityCode;
    private Integer toFacilityId;
    private String toFacilityName;
    private String toFacilityCode;
    private List<Container> containers;
    private long earlyDeliveryTime;
    private long lateDeliveryTime;
    private long earlyPickupTime;
    private long latePickupTime;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
