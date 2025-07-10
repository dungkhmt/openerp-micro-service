package openerp.openerpresourceserver.dto.response;

public record TodayDeliveryTripResponse(String deliveryTripId, String warehouseName, double distance,
		int totalLocations, String status) {
}
