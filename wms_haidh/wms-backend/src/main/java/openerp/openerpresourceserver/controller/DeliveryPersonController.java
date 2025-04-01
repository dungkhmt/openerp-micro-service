package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.projection.DeliveryPersonProjection;
import openerp.openerpresourceserver.service.DeliveryPersonService;

@RestController
@RequestMapping("/delivery-persons")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryPersonController {

	private DeliveryPersonService deliveryPersonService;
	
	@GetMapping("/paged")
	public ResponseEntity<Page<DeliveryPerson>> getAllDeliveryPersons(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "search", required = false) String search) {

		Page<DeliveryPerson> deliveryPersons = deliveryPersonService.getAllDeliveryPersons(page, size, search);
		return ResponseEntity.ok(deliveryPersons);
	}
	
	@GetMapping("/all")
	public List<DeliveryPersonProjection> getAllDeliveryPersons() {
		return deliveryPersonService.getAllDeliveryPersons();
	}
	
	@GetMapping("/{userLoginId}")
    public ResponseEntity<DeliveryPerson> getDeliveryPersonById(@PathVariable String userLoginId) {
        return deliveryPersonService.getDeliveryPersonById(userLoginId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
	
	@PostMapping
    public ResponseEntity<Boolean> createDeliveryPerson(@RequestBody DeliveryPersonCreateRequest request) {
		boolean success = deliveryPersonService.createDeliveryPerson(request);
        return ResponseEntity.ok(success);
    }
	
	@PostMapping("/update")
    public ResponseEntity<Boolean> updateDeliveryPerson(@RequestBody DeliveryPerson updatedPerson) {
        boolean success = deliveryPersonService.updateDeliveryPerson(updatedPerson);
        return ResponseEntity.ok(success);
    }
	
}
