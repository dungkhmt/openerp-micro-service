package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ProductResponse(UUID productId, String name, String imageUrl, double price, double quantity, String uom,
		double weight) {
}
