package openerp.openerpresourceserver.dto;

import lombok.Builder;
import lombok.Data;
import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class AssignOrderShipperDto {
    private UUID id;
    private UUID orderId;
    private UUID shipperId;
    private String shipperName;
    private int sequenceNumber;
    private String recipientAddress;
    private String recipientName;
    private String recipientPhone;
    private Double recipientLongitude;
    private Double recipientLatitude;
    private OrderStatus orderStatus;
    private ShipperAssignmentStatus status;
    private Timestamp orderCreatedAt;

}
