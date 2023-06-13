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
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_receipt_bill")
@EntityListeners(AuditingEntityListener.class)
public class ReceiptBill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wms_receipt_bill_seq")
    @GenericGenerator(name = "wms_receipt_bill_seq",
    strategy = "com.hust.wmsbackend.management.entity.generator.StringPrefixedSequenceCodeGenerator",
    parameters = {
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.VALUE_PREFIX_PARAMETER, value = "RB_"),
            @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
    })
    private String receiptBillId;
    private BigDecimal totalPrice;
    private String description;
    private UUID receiptId;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdStamp;
    @LastModifiedDate
    private Date lastUpdateStamp;

}
