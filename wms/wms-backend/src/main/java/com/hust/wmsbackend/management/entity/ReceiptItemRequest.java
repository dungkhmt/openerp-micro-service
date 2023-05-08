package com.hust.wmsbackend.management.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "receipt_item_request")
@EntityListeners(AuditingEntityListener.class)
public class ReceiptItemRequest {
    @Id
    private UUID receiptItemRequestId;
    private UUID receiptId;
    private UUID productId;
    private BigDecimal quantity;
    private UUID warehouseId;
}
