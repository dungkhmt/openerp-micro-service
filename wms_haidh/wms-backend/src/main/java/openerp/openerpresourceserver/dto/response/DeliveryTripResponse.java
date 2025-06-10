package openerp.openerpresourceserver.dto.response;

public record DeliveryTripResponse(String deliveryTripId, String deliveryPersonName, String warehouseName,
		double distance, int totalLocations, String status) {
}
