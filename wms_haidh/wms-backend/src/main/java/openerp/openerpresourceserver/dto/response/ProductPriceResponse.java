package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record ProductPriceResponse(UUID productId, String name, double price) {
}
