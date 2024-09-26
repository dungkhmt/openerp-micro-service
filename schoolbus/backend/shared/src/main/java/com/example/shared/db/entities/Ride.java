package com.example.shared.db.entities;

import com.example.shared.enumeration.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
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
@Table(name = "tieptd_194185_ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    private Instant startAt;

    private Instant endAt;

    @Column(name = "start_from")
    private String startFrom;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(name = "is_to_school", columnDefinition = "boolean default true")
    private Boolean isToSchool;

    @CreatedDate
    @CreationTimestamp
    private Instant createdAt;
    @LastModifiedDate
    @UpdateTimestamp
    private Instant updatedAt;

    public RideHistory toRideHistory(Long driverId, Long driverMateId) {
        return RideHistory.builder()
            .rideId(this.id)
            .busId(this.bus.getId())
            .startAt(this.startAt)
            .endAt(this.endAt)
            .startFrom(this.startFrom)
            .status(this.status)
            .isToSchool(this.isToSchool)
            .driverId(driverId)
            .driverMateId(driverMateId)
            .build();
    }
}
