package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record PickedOrderItemResponse(UUID assignedOrderItemId, String productName, int originalQuantity,
		String bayCode, String lotId, String status) {
}
