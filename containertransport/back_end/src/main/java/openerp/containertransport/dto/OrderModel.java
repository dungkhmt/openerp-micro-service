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
    private String uid;
    private String orderCode;
    private String type;
    private long weight;
    private Boolean isBreakRomooc;
    private ContainerModel containerModel;
    private String customerId;
    private List<Integer> containerIds;
    private FacilityResponsiveDTO fromFacility;
    private String fromFacilityId;
    private String toFacilityId;
    private FacilityResponsiveDTO toFacility;
    private List<Container> containers;
    private long earlyDeliveryTime;
    private long lateDeliveryTime;
    private long earlyPickupTime;
    private long latePickupTime;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String username;
}
