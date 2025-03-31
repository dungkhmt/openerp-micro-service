package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "smartdelivery_order_history")
public class OrderHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus newStatus;

    @Column
    private String changedBy;

    @Column(length = 1000)
    private String changeDetails;

    @Column
    private UUID performedById;

    @CreationTimestamp
    private Timestamp createdAt;
}