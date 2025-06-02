package openerp.openerpresourceserver.dto;

import lombok.Data;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequestDto {
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
    private Double recipientLongitude;
    private Double recipientLatitude;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;

    private List<OrderItem> items;

    private Double totalPrice;

    private Double shippingPrice;

    private Double finalPrice;

    private String orderType;

    private OrderStatus status;


}
