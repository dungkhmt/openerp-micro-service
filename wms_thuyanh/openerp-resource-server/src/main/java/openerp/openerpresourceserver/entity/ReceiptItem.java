package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
