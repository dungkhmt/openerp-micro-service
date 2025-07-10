package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "smartdelivery_shipper_assignment_history")
public class AssignOrderShipperHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    private UUID assignmentId;
    private UUID orderId;
    private UUID shipperId;

    @Enumerated(EnumType.STRING)
    private ShipperAssignmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private ShipperAssignmentStatus newStatus;

    private Integer deliveryAttempts;
    private String deliveryNotes;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    private Timestamp createdAt;
}