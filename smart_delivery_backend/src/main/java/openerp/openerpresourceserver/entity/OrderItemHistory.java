package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smartdelivery_order_item_history")
public class OrderItemHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    private UUID orderItemId;
    private UUID orderId;

    // Core OrderItem data
    private String name;
    private Integer quantity;
    private Double weight;
    private Double price;
    private Double length;
    private Double width;
    private Double height;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;
    private Integer version;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    private Timestamp createdAt;
}