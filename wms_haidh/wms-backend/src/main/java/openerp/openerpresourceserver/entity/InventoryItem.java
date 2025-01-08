package openerp.openerpresourceserver.entity;

import java.math.BigDecimal;
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
@Table(name = "wms_inventory_item")
public class InventoryItem {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID inventoryItemId;

    private UUID productId;
    private String lotId;
    private UUID warehouseId;
    private UUID bayId;
    private int quantityOnHandTotal;
    private BigDecimal importPrice;
    private String currencyUomId; 
    private LocalDateTime datetimeReceived;
    private LocalDateTime expireDate;
    private LocalDateTime lastUpdatedStamp;
    private LocalDateTime createdStamp;
    private String description; 
    private boolean isInitQuantity; 
}

