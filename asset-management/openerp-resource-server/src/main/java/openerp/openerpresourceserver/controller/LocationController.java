package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Location;
import openerp.openerpresourceserver.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewLocation(@RequestBody Location location){
        Location savedLocation = locationService.addNewLocation(location);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(savedLocation);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editLocation(@PathVariable Integer Id,
                                          @RequestBody Location location){
        Location savedLocation = locationService.editLocation(Id, location);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(savedLocation);
    }

    @DeleteMapping("/delete/{Id")
    public ResponseEntity<?> deleteLocation(@PathVariable Integer Id){
        locationService.deleteLocation(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
