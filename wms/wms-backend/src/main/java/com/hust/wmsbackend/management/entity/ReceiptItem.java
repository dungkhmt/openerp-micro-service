package com.hust.wmsbackend.management.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_receipt_item")
@EntityListeners(AuditingEntityListener.class)
public class ReceiptItem {

    @Id
    private UUID receiptItemId;
    private UUID receiptId;
    private UUID productId;
    private BigDecimal quantity;
    private UUID bayId;
    private String lotId;
    private BigDecimal importPrice;
    private Date expiredDate;
    private UUID receiptItemRequestId;
    private String receiptBillId;

    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedDate
    private Date createdStamp;

}
