package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record SaleOrderItemResponse(UUID saleOrderItemId, String productName, int quantity, String uom,
		double priceUnit, double completed) {
}
