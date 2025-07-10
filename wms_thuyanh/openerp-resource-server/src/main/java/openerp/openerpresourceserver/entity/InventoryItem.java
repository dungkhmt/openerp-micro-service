package openerp.openerpresourceserver.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_inventory_item")
@EntityListeners(AuditingEntityListener.class)
public class InventoryItem {
    @Id
    private UUID inventoryItemId;
    private UUID productId;
    private String lotId;
    private UUID warehouseId;
    private UUID bayId;

    private BigDecimal quantityOnHandTotal;
    private BigDecimal importPrice;

    private String currencyUomId;
    private Date datetimeReceived;
    private Date expireDate;
    @LastModifiedDate
    private Date lastUpdatedStamp;
    @CreatedDate
    private Date createdStamp;

    private String description;
    private boolean isInitQuantity;
}
