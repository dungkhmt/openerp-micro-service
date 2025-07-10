package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "wms_address_distance")
public class AddressDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID addressDistanceId;

    private UUID fromLocationId;

    @Enumerated(EnumType.STRING)
    private AddressType fromLocationType;

    private UUID toLocationId;

    @Enumerated(EnumType.STRING)
    private AddressType toLocationType;

    private double distance; // in meters

    private LocalDateTime lastUpdatedStamp;

    private LocalDateTime createdStamp;
}

