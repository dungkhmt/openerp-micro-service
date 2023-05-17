package com.hust.wmsbackend.management.entity;

import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripStatus;
import com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_delivery_trip")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wms_delivery_trip_seq")
    @GenericGenerator(name = "wms_delivery_trip_seq",
        strategy = "com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.VALUE_PREFIX_PARAMETER, value = "TRP_"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
        })
    private String deliveryTripId;
    private String shipmentId;
    private UUID vehicleId;
    private String deliveryPersonId;
    private BigDecimal distance;
    private BigDecimal totalWeight;
    private int totalLocations;
    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedDate
    private Date createdStamp;
    @CreatedBy
    private String createdBy;
    private boolean isDeleted = false;
    private UUID warehouseId;
    @Enumerated(EnumType.STRING)
    private DeliveryTripStatus status = DeliveryTripStatus.CREATED;
}
