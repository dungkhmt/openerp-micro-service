package openerp.openerpresourceserver.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutingResponse {

	 private double totalDistance; 
	    
	 private List<TruckDTO> trucks;

}
