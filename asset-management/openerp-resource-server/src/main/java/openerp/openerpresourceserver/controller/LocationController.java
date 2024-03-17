package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Location;
import openerp.openerpresourceserver.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/location")
public class LocationController {
    private LocationService locationService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllLocations(){
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(locations);
    }
}
