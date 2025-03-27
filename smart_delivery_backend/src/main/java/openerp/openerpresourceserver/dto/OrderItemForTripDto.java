package openerp.openerpresourceserver.dto;

import lombok.Data;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class OrderItemForTripDto {
    public OrderItemForTripDto(OrderItem orderItem){
        this.id = orderItem.getOrderItemId();
        this.createdAt= orderItem.getCreatedAt();
        this.status = String.valueOf(orderItem.getStatus());
        this.weight = orderItem.getWeight();
        this.price = orderItem.getPrice();
        this.length = orderItem.getLength();
        this.width = orderItem.getWidth();
        this.height = orderItem.getHeight();
        this.volume = orderItem.getLength() * orderItem.getWidth() * orderItem.getHeight()/100/100/100;
    }

    private UUID id;
    private UUID orderId;
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String status;
    private Double weight;
    private Double price;
    private Double length;
    private Double width;
    private Double height;
    private Double volume;
    private Timestamp createdAt;

}
