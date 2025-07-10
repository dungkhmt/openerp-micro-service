package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record CustomerOrderResponse(UUID customerAddressId, String customerName, String customerPhoneNumber,
		String addressName, double longitude, double latitude) {
}
