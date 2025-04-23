package openerp.openerpresourceserver.dto.request;

import java.util.Objects;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class CoordinateDTO {
	private double lng;
	private double lat;
	
	public CoordinateDTO(double lng, double lat) {
	    this.lng = lng;
	    this.lat = lat;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CoordinateDTO that = (CoordinateDTO) o;
		return Double.compare(that.lat, lat) == 0 && Double.compare(that.lng, lng) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lat, lng);
	}
}
