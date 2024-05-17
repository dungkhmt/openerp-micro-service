package com.hust.wmsbackend.v2.model;

import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import com.hust.wmsbackend.management.entity.enumentity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class OrderDetailDTO {
    @Id
    private UUID orderId;
    private String userLoginId;
    private String customerName;
    private PaymentType paymentType;
    private OrderStatus status;
    private Date createdStamp;
    private BigDecimal totalOrderCost;
    private String addressName;
    private BigDecimal longitude;
    private BigDecimal latitude;

    public OrderDetailDTO(UUID orderId, String userLoginId, String customerName, PaymentType paymentType, OrderStatus status,
                          Date createdStamp, BigDecimal totalOrderCost,
                          String addressName, BigDecimal longitude, BigDecimal latitude) {
        this.orderId = orderId;
        this.userLoginId = userLoginId;
        this.customerName = customerName;
        this.paymentType = paymentType;
        this.status = status;
        this.createdStamp = createdStamp;
        this.totalOrderCost = totalOrderCost;
        this.addressName = addressName;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
