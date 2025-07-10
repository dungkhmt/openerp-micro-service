package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "wms_shipment")
public class Shipment {
    @Id
    private String shipmentId;
    private LocalDateTime expectedDeliveryStamp;
    private LocalDateTime createdStamp;
    private LocalDateTime lastUpdatedStamp;
    private String createdBy;
}
