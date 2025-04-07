package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Updated Trip entity to better support driver trip management
 */
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

    /**
     * Reference to the route this trip is on
     * Note: This now references the Route directly, not RouteVehicle
     */
    @Column(name = "route_schelude_id", nullable = false)
    private UUID routeScheduleId;
    private UUID vehicleId;
    /**
     * The driver assigned to this trip
     */
    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    private LocalDate date;

    @Column(name = "start_time", nullable = true)
    private Instant startTime;

    /**
     * When the trip was completed
     */
    @Column(name = "end_time")
    private Instant endTime;

    /**
     * Current status of the trip
     * Possible values: "PLANNED", "IN_PROGRESS", "CONFIRMED_IN","COMPLETED", "CANCELLED"
     */
    @Column(name = "status", nullable = false)
    private String status;

    /**
     * Current position in the route stop sequence
     * e.g., 0 means at first stop, 1 means at second stop, etc.
     */
    @Column(name = "current_stop_index")
    private Integer currentStopIndex;

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
    @Version
    private Integer version;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}