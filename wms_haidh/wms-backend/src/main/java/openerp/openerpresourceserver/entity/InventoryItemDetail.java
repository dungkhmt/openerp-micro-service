package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "wms_inventory_item_detail")
public class InventoryItemDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID inventoryItemDetailId;

    private UUID inventoryItemId;
    private int quantityOnHandDiff; // change in quantity on hand total 
    private LocalDateTime effectiveDate;
}

