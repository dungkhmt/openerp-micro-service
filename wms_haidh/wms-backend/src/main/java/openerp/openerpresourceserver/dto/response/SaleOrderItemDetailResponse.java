package openerp.openerpresourceserver.dto.response;

public record SaleOrderItemDetailResponse(String productName, int quantity, double priceUnit, String uom,
		double completed, String addressName) {
}