package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record DeliveryItemDetailResponse(UUID id, String productName, double weight,  int quantity, String uom,
		String bayCode, String lotId) {
}
