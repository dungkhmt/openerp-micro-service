package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.DeliveryPersonCreateRequest;
import openerp.openerpresourceserver.dto.response.DeliveryPersonResponse;
import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.service.DeliveryPersonService;

@RestController
@RequestMapping("/delivery-persons")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryPersonController {

	private DeliveryPersonService deliveryPersonService;
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/paged")
	public ResponseEntity<Page<DeliveryPerson>> getAllDeliveryPersons(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "AVAILABLE") String status) {

		Page<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons(page, size, search, status);
		return ResponseEntity.ok(deliveryPersons);
	}
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/available")
	public List<DeliveryPersonResponse> getAllDeliveryPersons() {
		return deliveryPersonService.getAllDeliveryPersons();
	}
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/{userLoginId}")
    public ResponseEntity<DeliveryPerson> getDeliveryPersonById(@PathVariable String userLoginId) {
        return deliveryPersonService.getDeliveryPersonById(userLoginId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping
    public ResponseEntity<Boolean> createDeliveryPerson(@RequestBody DeliveryPersonCreateRequest request) {
		boolean success = deliveryPersonService.createDeliveryPerson(request);
        return ResponseEntity.ok(success);
    }
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/update")
    public ResponseEntity<Boolean> updateDeliveryPerson(@RequestBody DeliveryPerson updatedPerson) {
        boolean success = deliveryPersonService.updateDeliveryPerson(updatedPerson);
        return ResponseEntity.ok(success);
    }
	
}
