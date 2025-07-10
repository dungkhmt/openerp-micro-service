package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ReceiptItemRequestResponse(UUID receiptItemRequestId, int quantity, double completed,
		String productName, String uom) {
}