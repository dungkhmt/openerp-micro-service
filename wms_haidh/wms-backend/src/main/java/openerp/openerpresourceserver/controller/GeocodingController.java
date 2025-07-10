package openerp.openerpresourceserver.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.response.AddressResponse;
import openerp.openerpresourceserver.service.geocoding.GeocodingService;

@RestController
@RequestMapping("/geocoding")
@AllArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @Secured("ROLE_WMS_ONLINE_CUSTOMER")
    @PostMapping("/address")
    public AddressResponse getAddressFromCoordinates(@RequestBody CoordinateDTO coordinates) {
        return geocodingService.getAddressFromCoordinates(coordinates);
    }
}

