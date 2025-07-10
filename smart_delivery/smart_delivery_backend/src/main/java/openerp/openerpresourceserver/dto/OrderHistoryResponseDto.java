package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryResponseDto {
    private UUID id;
    private UUID orderId;
    private OrderStatus status;
    private Integer version;
    private String changedBy;
    
    // Core order data at the time of change
    private UUID senderId;
    private String senderName;
    private UUID recipientId;
    private String recipientName;
    private String orderType;
    private Double totalPrice;
    private Double shippingPrice;
    private Double finalPrice;
    private String origin;
    private String destinationAddress;
    private Date expectedDeliveryDate;
    private UUID originHubId;
    private String originHubName;
    private UUID finalHubId;
    private String finalHubName;
    
    // Change metadata
    private String changeReason;
    private Timestamp originalCreatedAt;
    private String originalCreatedBy;
    private Timestamp createdAt;
} 