package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;

public record ReceiptItemResponse(int quantity, String bayCode, double importPrice, LocalDateTime expiredDate,
		String lotId) {
}
