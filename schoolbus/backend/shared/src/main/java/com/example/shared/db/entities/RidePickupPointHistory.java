package com.example.shared.db.entities;

import com.example.shared.enumeration.RidePickupPointStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tieptd_194185_ride_pickup_point_history")
public class RidePickupPointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ride_pickup_point_id")
    private Long ridePickupPointId;

    @Column(name = "pickup_point_id")
    private Long pickupPointId;

    @Column(name = "ride_id")
    private Long rideId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    private RidePickupPointStatus status;

    private String address;

    private Double longitude;

    private Double latitude;

    @CreatedDate
    @CreationTimestamp
    private Instant createdAt;
    @LastModifiedDate
    @UpdateTimestamp
    private Instant updatedAt;

    public static RidePickupPointHistory fromEntity(RidePickupPoint ridePickupPoint) {
        return RidePickupPointHistory.builder()
            .ridePickupPointId(ridePickupPoint.getId())
            .pickupPointId(ridePickupPoint.getPickupPoint().getId())
            .rideId(ridePickupPoint.getRide().getId())
            .status(ridePickupPoint.getStatus())
            .orderIndex(ridePickupPoint.getOrderIndex())
            .address(ridePickupPoint.getPickupPoint().getAddress())
            .longitude(ridePickupPoint.getPickupPoint().getLongitude())
            .latitude(ridePickupPoint.getPickupPoint().getLatitude())
            .build();
    }
}
