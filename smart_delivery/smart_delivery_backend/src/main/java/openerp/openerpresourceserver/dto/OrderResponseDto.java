package openerp.openerpresourceserver.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.mapper.SenderMapper;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String senderPhone;
    private String senderEmail;
    private String senderAddress;
    private Double senderLongitude;
    private Double senderLatitude;
    private UUID recipientId;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String recipientAddress;
    private String recipientLongitude;
    private String recipientLatitude;

    private List<OrderItem> items;
    private String orderType;
    private OrderStatus status;
    private Double totalPrice;
    private Double shippingPrice;
    private Double finalPrice;
    private Date expectedDeliveryDate;

    private String createdBy;
    private String approvedBy;
    private String cancelledBy;
    private Timestamp createdAt;

}
