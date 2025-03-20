package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "smartdelivery_trip")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "route_vehicle_id", nullable = false)
    private UUID routeVehicleId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "start_time", nullable = true)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "status", nullable = false)
    private String status; // "PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"

    /**
     * Current position in the route stop sequence
     * e.g., 0 means at first stop, 1 means at second stop, etc.
     */
    @Column(name = "current_stop_index")
    private Integer currentStopIndex = 1;

    /**
     * Time of arrival at the current stop
     */
    @Column(name = "last_stop_arrival_time")
    private Instant lastStopArrivalTime;

    /**
     * Total distance traveled during this trip (in km)
     */
    @Column(name = "distance_traveled")
    private Double distanceTraveled;

    /**
     * Notes provided when completing the trip
     */
    @Column(name = "completion_notes", length = 500)
    private String completionNotes;

    /**
     * Total number of orders picked up during this trip
     */
    @Column(name = "orders_picked_up")
    private Integer ordersPickedUp;

    /**
     * Total number of orders delivered during this trip
     */
    @Column(name = "orders_delivered")
    private Integer ordersDelivered;

    /**
     * JSON array of delay events
     * e.g., traffic, vehicle issues, weather
     */
    @Column(name = "delay_events", columnDefinition = "TEXT")
    private String delayEvents;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}