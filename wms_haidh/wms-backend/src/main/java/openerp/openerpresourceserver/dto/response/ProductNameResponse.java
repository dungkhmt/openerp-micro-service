package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ProductNameResponse(UUID productId, String name, String uom) {
}