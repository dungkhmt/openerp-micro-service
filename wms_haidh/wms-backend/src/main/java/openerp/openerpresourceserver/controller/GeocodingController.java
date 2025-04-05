package openerp.openerpresourceserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinatesDTO;
import openerp.openerpresourceserver.dto.response.AddressResponseDTO;
import openerp.openerpresourceserver.service.geocoding.GeocodingService;

@RestController
@RequestMapping("/geocoding")
@AllArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/address")
    public AddressResponseDTO getAddressFromCoordinates(@RequestBody CoordinatesDTO coordinates) {
        return geocodingService.getAddressFromCoordinates(coordinates);
    }
}

