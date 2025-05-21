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

    private String deliveryPersonId;

    private double distance;  // in meters

    private double totalWeight; // in kg

    private int totalLocations;

    private LocalDateTime lastUpdatedStamp;

    private LocalDateTime createdStamp;

    private String createdBy;

    private String shipmentId;

    private UUID warehouseId;

    private String status;

    private String description;
}
