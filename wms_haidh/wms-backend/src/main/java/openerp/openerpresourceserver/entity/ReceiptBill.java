package openerp.openerpresourceserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
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
@Table(name = "wms_receipt_bill")
public class ReceiptBill {

    @Id
    private String receiptBillId;

    private String description;

    private UUID receiptId;

    private String createdBy;

    private LocalDateTime createdStamp;

    private LocalDateTime lastUpdateStamp;

    private BigDecimal totalPrice;
}

