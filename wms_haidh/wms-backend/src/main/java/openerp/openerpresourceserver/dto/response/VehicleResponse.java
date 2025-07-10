package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record VehicleResponse(UUID vehicleId, String name, double maxWeight) {
}
