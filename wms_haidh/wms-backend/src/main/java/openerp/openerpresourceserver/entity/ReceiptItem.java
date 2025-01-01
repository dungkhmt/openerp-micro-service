package openerp.openerpresourceserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_receipt_item")
public class ReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID receiptItemId;

    private UUID receiptId;
    private UUID productId;
    private int quantity;
    private UUID bayId;
    private String lotId;
    private BigDecimal importPrice;
    private String expiredDate;

    @Column(name = "last_updated_stamp", nullable = false)
    private LocalDateTime lastUpdatedStamp;

    @Column(name = "created_stamp", nullable = false)
    private LocalDateTime createdStamp;

    private UUID receiptItemRequestId;
    private UUID receiptBillId;

}

