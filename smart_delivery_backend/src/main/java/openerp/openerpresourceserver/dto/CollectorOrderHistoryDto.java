package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectorOrderHistoryDto {
    private UUID assignmentId;
    private UUID orderId;
    private UUID collectorId;
    private String collectorName;
    private Integer sequenceNumber;
    private CollectorAssignmentStatus assignmentStatus;
    private OrderStatus orderStatus;

    // Order details
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private Double senderLatitude;
    private Double senderLongitude;

    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;

    // Timestamps
    private Timestamp assignmentCreatedAt;
    private Timestamp assignmentUpdatedAt;
    private Timestamp orderCreatedAt;
    private Timestamp doneAt;

    // Additional info
    private Integer numOfItems;
    private Double totalWeight;
    private String notes;
}