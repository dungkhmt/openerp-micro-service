package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "smartdelivery_route_stop",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_route_stop_sequence",
                        columnNames = {"route_id", "stop_sequence"}
                ),
                @UniqueConstraint(
                        name = "uk_route_hub",
                        columnNames = {"route_id", "hub_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStop {

    @Id
    private UUID id;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;


    @Column(name = "estimated_wait_time")
    private Integer estimatedWaitTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.valueOf(LocalDateTime.now());
        this.updatedAt = Timestamp.valueOf(LocalDateTime.now());
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }

}