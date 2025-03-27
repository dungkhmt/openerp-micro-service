package openerp.openerpresourceserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.Order;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class OrderForTripDto {


        public OrderForTripDto(Order order){
            this.id = order.getId();
            this.senderId = order.getSenderId();
            this.senderName = order.getSenderName();
            this.recipientId = order.getRecipientId();
            this.recipientName = order.getRecipientName();
            this.orderType = order.getOrderType();
            this.status = String.valueOf(order.getStatus());
            this.totalPrice = order.getTotalPrice();
            this.shippingPrice = order.getShippingPrice();
            this.finalPrice = order.getFinalPrice();
            this.createdAt = order.getCreatedAt();
        }

        private UUID id;
        private UUID senderId;
        private UUID recipientId;
        private String senderName;
        private String recipientName;
        private String orderType;
        private String status;
        private Double totalPrice;
        private Double shippingPrice;
        private Double orderVolume;
        private Double orderWeight;
        private Double finalPrice;
        private Timestamp createdAt;
        private int packagesCount;


}
