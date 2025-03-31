package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "smartdelivery_trip_history")
public class TripHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    @Column(nullable = false)
    private UUID tripId;

    private String previousStatus;
    private String newStatus;

    private Integer previousStopIndex;
    private Integer newStopIndex;

    private UUID driverId;
    private String driverName;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    private Timestamp createdAt;
}