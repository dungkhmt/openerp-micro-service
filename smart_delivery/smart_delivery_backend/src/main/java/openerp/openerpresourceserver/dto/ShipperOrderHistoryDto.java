package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperOrderHistoryDto {
    private UUID assignmentId;
    private UUID orderId;
    private UUID shipperId;
    private String shipperName;
    private Integer sequenceNumber;
    private ShipperAssignmentStatus assignmentStatus;
    private OrderStatus orderStatus;

    // Order details
    private String senderName;
    private String senderPhone;
    private String senderAddress;

    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private Double recipientLatitude;
    private Double recipientLongitude;

    // Timestamps
    private Timestamp assignmentCreatedAt;
    private Timestamp assignmentUpdatedAt;
    private Timestamp orderCreatedAt;
    private Timestamp doneAt;

    // Additional info
    private Integer deliveryAttempts;
    private String failureReason;
    private String notes;
}