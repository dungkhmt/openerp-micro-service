package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryItemResponse(UUID inventoryItemId, String productName, String lotId, int availableQuantity,
		int quantityOnHandTotal, LocalDateTime lastUpdatedStamp) {
}
