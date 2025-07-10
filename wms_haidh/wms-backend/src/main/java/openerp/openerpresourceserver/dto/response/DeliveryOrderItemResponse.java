package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record DeliveryOrderItemResponse(UUID assignedOrderItemId, UUID orderId, String productName, double weight,
		int originalQuantity, String bayCode, String lotId) {
}
