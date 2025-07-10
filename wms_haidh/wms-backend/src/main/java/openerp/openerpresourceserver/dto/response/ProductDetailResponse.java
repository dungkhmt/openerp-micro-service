package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ProductDetailResponse(UUID productId, String code, String name, String description, double height,
		double weight, String uom, String imageUrl, double price, double quantity) {
}
