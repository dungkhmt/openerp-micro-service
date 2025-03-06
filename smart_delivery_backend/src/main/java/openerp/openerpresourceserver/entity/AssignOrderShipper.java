package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "smartdelivery_assign_order_shipper")
public class AssignOrderShipper {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private UUID orderId;

    private UUID shipperId;

    private String shipperName;

    private Long version;

    private int sequenceNumber;

    @Enumerated(EnumType.STRING)
    private ShipperAssignmentStatus status;

    private String deliveryNotes;

    private String confirmationType;  // "otp", "signature"

    private String confirmationValue;

    private Integer deliveryAttempts;

    @CreatedBy
    private String createdBy;

    private String approvedBy;

    private String cancelledBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}