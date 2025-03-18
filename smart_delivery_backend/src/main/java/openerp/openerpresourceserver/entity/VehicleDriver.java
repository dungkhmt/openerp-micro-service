package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "smartdelivery_vehicle_driver")
public class VehicleDriver {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    private UUID vehicleId;
    private UUID driverId;

    @UpdateTimestamp
    private Timestamp updatedAt;
    @CreationTimestamp
    private Timestamp assignedAt;
    private Timestamp unassignedAt;


}
