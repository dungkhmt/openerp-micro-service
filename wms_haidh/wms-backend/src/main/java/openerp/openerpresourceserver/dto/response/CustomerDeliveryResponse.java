package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record CustomerDeliveryResponse(UUID orderId, String customerName, String customerPhoneNumber,
		String customerAddress, int sequence, String status) {
}
