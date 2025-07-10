package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ReceiptItemRequestDetailResponse(UUID receiptItemRequestId, int quantity, double completed,
		String productName, String uom, String warehouseName) {
}
