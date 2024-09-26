package com.example.shared.db.entities;

import com.example.shared.enumeration.StudentPickupPointStatus;
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
@Table(name = "tieptd_194185_student_pickup_point_history")
public class StudentPickupPointHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_pickup_point_id")
    private Long studentPickupPointId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "pickup_point_id")
    private Long pickupPointId;

    @Enumerated(EnumType.STRING)
    private StudentPickupPointStatus status;

    private String address;

    private Double latitude;

    private Double longitude;

    @Column(name = "ride_id")
    private Long rideId;

    @CreatedDate
    @CreationTimestamp
    private Instant createdAt;
    @LastModifiedDate
    @UpdateTimestamp
    private Instant updatedAt;

    public StudentPickupPointHistory (StudentPickupPoint studentPickupPoint,
                                      String address, Double latitude, Double longitude,
                                      Long rideId) {
        this.studentPickupPointId = studentPickupPoint.getId();
        this.studentId = studentPickupPoint.getStudent().getId();
        this.pickupPointId = studentPickupPoint.getPickupPoint().getId();
        this.status = studentPickupPoint.getStatus();
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rideId = rideId;
    }
}
