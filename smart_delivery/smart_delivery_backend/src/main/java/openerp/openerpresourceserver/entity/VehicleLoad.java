package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Entity to track the current load on vehicles
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "smartdelivery_vehicle_load")
public class VehicleLoad {

    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID id;

    @Column(nullable = false)
    private UUID vehicleId;

    @Column(nullable = false)
    private Double currentWeightLoad;

    private Double currentVolumeLoad;

    private Integer currentItemCount;

    @UpdateTimestamp
    private Timestamp lastUpdated;

    public VehicleLoad(UUID vehicleId) {
        this.vehicleId = vehicleId;
        this.currentWeightLoad = 0.0;
        this.currentVolumeLoad = 0.0;
        this.currentItemCount = 0;
    }
}