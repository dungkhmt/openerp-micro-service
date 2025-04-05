package openerp.openerpresourceserver.service.geocoding;

import openerp.openerpresourceserver.dto.request.CoordinatesDTO;
import openerp.openerpresourceserver.dto.response.AddressResponseDTO;

public interface GeocodingService {
	
 AddressResponseDTO getAddressFromCoordinates(CoordinatesDTO coordinates);
 
}

