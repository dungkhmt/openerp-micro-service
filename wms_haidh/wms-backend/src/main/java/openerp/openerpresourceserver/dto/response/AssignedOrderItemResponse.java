package openerp.openerpresourceserver.dto.response;

public record AssignedOrderItemResponse(String warehouseName, String bayCode, String lotId, int quantity,
		String status) {
}
