package openerp.openerpresourceserver.dto.response;

import java.util.UUID;

public record AddressDistanceResponse(UUID addressDistanceId, String fromLocationName, String toLocationName,
		double distance) {
}
