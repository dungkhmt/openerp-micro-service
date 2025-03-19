package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "smartdelivery_trip")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID routeVehicleId;  // Reference to the route-vehicle assignment

    @Column(nullable = false)
    private LocalDate tripDate;  // The day of this specific trip

    @Column
    private UUID driverId;  // The driver assigned to this trip

    @Column
    private String driverName;  // For quick reference

    @Column
    private Timestamp departureTime;  // When the trip actually started

    @Column
    private Timestamp completionTime;  // When the trip was completed

    @Column(nullable = false)
    private Integer currentStopSequence = 1;  // Current stop in the route

    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.PENDING;  // PENDING, IN_PROGRESS, COMPLETED, etc.

    @Column
    private String notes;  // Any notes about this specific trip

    // For monitoring and metrics
    @Column
    private Integer totalOrders;  // Total orders handled in this trip

    @Column
    private Double totalWeight;  // Total weight transported

    @Column
    private Integer completedStops;  // Number of stops completed

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    // Getters and setters...
}
