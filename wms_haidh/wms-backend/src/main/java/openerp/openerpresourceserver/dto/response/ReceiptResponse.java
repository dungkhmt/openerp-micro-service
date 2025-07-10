package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;

public record ReceiptResponse(String receiptName, String description, String warehouseName, String supplierName,
		LocalDateTime expectedReceiptDate, String status, String createdBy, LocalDateTime createdStamp) {
}
