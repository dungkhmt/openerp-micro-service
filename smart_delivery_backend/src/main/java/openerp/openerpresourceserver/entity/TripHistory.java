package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "smartdelivery_trip_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripHistory {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    @Column(nullable = false)
    private UUID tripId;

    @Column(nullable = false)
    private String changedBy;

    @CreationTimestamp
    private Instant createdAt;

    // Previous state
    private String status;
    private Integer currentStopIndex;
    private Integer ordersPickedUp;

    // Information for reference
    private UUID driverId;
    private Integer version;
    @Column(length = 500)
    private String notes;
}