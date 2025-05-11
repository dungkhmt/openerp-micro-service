package openerp.openerpresourceserver.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedOrderItemCreateRequest {

	private UUID warehouseId;
	private UUID bayId;
	private String lotId;
	private int quantity;
	private UUID saleOrderItemId;
}