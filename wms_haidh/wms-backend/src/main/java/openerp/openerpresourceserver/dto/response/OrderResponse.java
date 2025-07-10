package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(UUID orderId, LocalDateTime orderDate, double totalOrderCost, String customerName,
		String status, String approvedBy, String cancelledBy) {
}
