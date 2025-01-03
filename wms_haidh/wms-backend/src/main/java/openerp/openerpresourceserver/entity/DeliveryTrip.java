package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_delivery_trip")
public class DeliveryTrip {

    @Id
    private String deliveryTripId;

    private UUID vehicleId;

    private UUID deliveryPersonId;

    private double distance;

    private double totalWeight;

    private int totalLocations;

    private LocalDateTime lastUpdatedStamp;

    private LocalDateTime createdStamp;

    private String createdBy;

    private boolean isDeleted;

    private UUID shipmentId;

    private UUID warehouseId;

    private String status;

    private String description;
}
