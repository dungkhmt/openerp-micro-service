package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.UpdateDistanceRequest;
import openerp.openerpresourceserver.entity.AddressType;
import openerp.openerpresourceserver.service.AddressDistanceService;

@RestController
@RequestMapping("/address-distances")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AddressDistanceController {

	private AddressDistanceService addressDistanceService;

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping
	public ResponseEntity<?> getDistanceList(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "WAREHOUSE") AddressType fromType,
			@RequestParam(defaultValue = "CUSTOMER") AddressType toType,
			@RequestParam(required = false) String fromLocation, @RequestParam(required = false) String toLocation) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdatedStamp").descending());
			return ResponseEntity.ok(addressDistanceService.getAllDistances(fromType, toType, fromLocation, toLocation, pageable));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured.");
		}
		
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/update")
	public ResponseEntity<Boolean> updateDistance(@RequestBody UpdateDistanceRequest request) {
		try {
			boolean updated = addressDistanceService.updateAddressDistance(request);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/update-all")
    public ResponseEntity<String> updateDistances() {
        try {
            addressDistanceService.updateDistances(); 
            return ResponseEntity.ok("Distances updated successfully."); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred while updating distances.");
        }
    }

}
