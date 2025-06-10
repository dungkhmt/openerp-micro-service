package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;

public record DeliveryTripGeneralResponse(double distance, double totalWeight, int totalLocations, String status,
		String description, LocalDateTime expectedDeliveryStamp, String warehouseName, String deliveryPersonName,
		String vehicleName) {
}
