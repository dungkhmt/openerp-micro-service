package com.hust.wmsbackend.management.entity;

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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_delivery_bill")
@EntityListeners(AuditingEntityListener.class)
public class DeliveryBill {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wms_delivery_bill_seq")
    @GenericGenerator(
        name = "wms_delivery_bill_seq",
        strategy = "com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.VALUE_PREFIX_PARAMETER, value = "DB_"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
        }
    )
    private String deliveryBillId;
    private BigDecimal totalPrice;
    private String description;
    private String deliveryTripId;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdStamp;
    @LastModifiedDate
    private Date lastUpdateStamp;
}
