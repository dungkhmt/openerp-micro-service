package openerp.openerpresourceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class CoordinatePair {
	private CoordinateDTO from;
	private CoordinateDTO to;
}

