package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;

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
@Table(name = "wms_delivery_trip_path")
public class DeliveryTripPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryTripPathId;

    private String deliveryTripId;

    private double longitude;

    private double latitude;

    private LocalDateTime createdStamp;
}