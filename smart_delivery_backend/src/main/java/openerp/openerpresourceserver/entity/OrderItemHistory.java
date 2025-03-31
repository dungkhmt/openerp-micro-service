package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import openerp.openerpresourceserver.entity.enumentity.OrderItemStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "smartdelivery_order_item_history")
public class OrderItemHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    private UUID orderItemId;
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus newStatus;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    private Timestamp createdAt;
}