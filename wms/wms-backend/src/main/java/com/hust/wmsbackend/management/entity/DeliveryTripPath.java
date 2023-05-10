package com.hust.wmsbackend.management.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_delivery_trip_path")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryTripPath {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_trip_path_seq")
    @SequenceGenerator(name = "delivery_trip_path_seq", allocationSize = 1) // TODO: auto reset on new delivery_trip_id
    private Long deliveryTripPathId;
    private String deliveryTripId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    @CreatedDate
    private Date createdStamp;
}
