package com.hust.wmsbackend.management.entity;

import com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_shipment")
@EntityListeners(AuditingEntityListener.class)
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wms_shipment_seq")
    @GenericGenerator(name = "wms_shipment_seq",
    strategy = "com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator",
    parameters = {
        @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.INCREMENT_PARAM, value = "1"),
        @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.VALUE_PREFIX_PARAMETER, value = "SP_"),
        @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
    })
    private String shipmentId;
    private Date expectedDeliveryStamp;
    @CreatedDate
    private Date createdStamp;
    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedBy
    private String createdBy;
    private boolean isDeleted = false;
}
