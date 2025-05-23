package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "wms_delivery_trip_item")
public class DeliveryTripItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID deliveryTripItemId;

    private String deliveryTripId;
    
    private int sequence;

    private UUID orderId;

    private UUID assignedOrderItemId;

    private int quantity;

    private String status;

    private LocalDateTime lastUpdatedStamp;

    private LocalDateTime createdStamp;
}

