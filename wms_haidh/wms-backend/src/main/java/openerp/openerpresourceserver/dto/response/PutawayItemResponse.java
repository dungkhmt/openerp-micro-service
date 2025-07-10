package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record PutawayItemResponse(UUID receiptItemId, String productName, int quantity, String lotId, String bayCode,
		String status) {
}
