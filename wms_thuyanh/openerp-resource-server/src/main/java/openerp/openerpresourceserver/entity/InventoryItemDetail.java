package openerp.openerpresourceserver.entity;

import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_inventory_item_detail")
public class InventoryItemDetail {
    @Id
    private UUID inventoryItemDetailId;
    private UUID inventoryItemId;
    private BigDecimal quantityOnHandDiff;
    private Date effectiveDate;
}
