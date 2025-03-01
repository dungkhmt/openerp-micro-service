package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AssignOrderCollectorDTO {
    private UUID id;
    private UUID orderId;
    private int sequenceNumber;
    private String senderAddress;
    private String senderName;
    private String senderPhone;
    private Double senderLongitude;
    private Double senderLatitude;
    private OrderStatus status;
    private Timestamp orderCreatedAt;
    private CollectorAssignmentStatus assignmentStatus;
    private Long numOfItem;
    public AssignOrderCollectorDTO(UUID id, UUID orderId, int sequenceNumber, String senderAddress,String senderName, String senderPhone,Double senderLongitude, Double senderLatitude,OrderStatus status, Timestamp orderCreatedAt,CollectorAssignmentStatus assignmentStatus, int numOfItem )
    {
        this.orderId = orderId;
        this.sequenceNumber = sequenceNumber;
        this.senderAddress = senderAddress;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.senderLongitude = senderLongitude;
        this.senderLatitude = senderLatitude;
        this.status = status;
        this.orderCreatedAt = orderCreatedAt;
        this.assignmentStatus = assignmentStatus;
        this.numOfItem = (long) numOfItem;
    }
}
