package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductInventoryResponse(UUID id, String code, String name, double totalQuantityOnHand,
		LocalDateTime dateUpdated) {
}
