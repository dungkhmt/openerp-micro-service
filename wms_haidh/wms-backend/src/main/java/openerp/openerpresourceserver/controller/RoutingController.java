package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.RoutingRequest;
import openerp.openerpresourceserver.dto.response.RoutingResponse;
import openerp.openerpresourceserver.service.GreedyInsertionService;

@RestController
@RequestMapping("/routing-recommendations")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RoutingController {

	private GreedyInsertionService greedyInsertionService;

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping
	public ResponseEntity<RoutingResponse> getRoute(@RequestBody RoutingRequest request) {
	    try {
	        RoutingResponse response = greedyInsertionService.greedyInsert(request);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}


}
