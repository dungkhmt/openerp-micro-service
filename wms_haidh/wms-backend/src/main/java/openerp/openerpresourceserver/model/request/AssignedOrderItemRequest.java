package openerp.openerpresourceserver.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedOrderItemRequest {

	private UUID warehouseId;
	private UUID bayId;
	private String lotId;
	private int quantity;
	private UUID saleOrderItemId;
	private String assignedBy;
}