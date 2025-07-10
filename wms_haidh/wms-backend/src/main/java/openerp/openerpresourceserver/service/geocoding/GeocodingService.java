package openerp.openerpresourceserver.service.geocoding;

import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.response.AddressResponse;

/**
 * General interface for geocoding services 
 */
public interface GeocodingService {

    /**
     * Retrieves a formatted address based on the given geographic coordinates.
     *
     * @param coordinates the {@link CoordinateDTO} object containing latitude and longitude
     * @return an {@link AddressResponse} object containing the formatted address
     */
    AddressResponse getAddressFromCoordinates(CoordinateDTO coordinates);
}


