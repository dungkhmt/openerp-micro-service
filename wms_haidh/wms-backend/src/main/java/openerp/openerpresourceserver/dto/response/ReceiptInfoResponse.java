package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReceiptInfoResponse(UUID receiptId, String receiptName, String warehouseName,
		LocalDateTime expectedReceiptDate, String status, String createdBy, String approvedBy, String cancelledBy) {
}
