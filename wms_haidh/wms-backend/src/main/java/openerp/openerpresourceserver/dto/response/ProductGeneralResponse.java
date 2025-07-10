package openerp.openerpresourceserver.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductGeneralResponse(UUID id, String code, String name, LocalDateTime dateUpdated) {
}
