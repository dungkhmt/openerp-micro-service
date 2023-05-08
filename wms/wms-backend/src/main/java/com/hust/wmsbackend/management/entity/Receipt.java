package com.hust.wmsbackend.management.entity;

import com.hust.wmsbackend.management.entity.enumentity.ReceiptStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "receipt")
@EntityListeners(AuditingEntityListener.class)
public class Receipt {

    @Id
    private UUID receiptId;
    private Date receiptDate;
    private String receiptName;
    private UUID warehouseId;
    private String description;
    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedDate
    private Date createdStamp;
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;
    private String createdReason;
    private Date expectedReceiptDate;
    @CreatedBy
    private String createdBy;
    private String approvedBy;
    private String cancelledBy;

}
