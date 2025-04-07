package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "smartdelivery_order_history")
public class OrderHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Integer version;

    @Column
    private String changedBy;

    // Core order data
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
    private Timestamp originalCreatedAt;
    private String originalCreatedBy;

    // Optional change reason
    private String changeReason;

    @CreationTimestamp
    private Timestamp createdAt;
}