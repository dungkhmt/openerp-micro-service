package com.hust.wmsbackend.management.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "sale_order_item")
@EntityListeners(AuditingEntityListener.class)
public class SaleOrderItem {
    @Id
    private UUID saleOrderItemId;
    private UUID orderId;
    private UUID productId;
    private long quantity;
    private BigDecimal priceUnit;
}
