package openerp.openerpresourceserver.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.service.route.RouteService;

@RestController
@RequestMapping("/routes")
@AllArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/fetch")
    public ResponseEntity<String> getRoute(@RequestBody Map<String, Object> requestBody) {
    	ResponseEntity<String> response = routeService.fetchRoute(requestBody);
    	return ResponseEntity.ok(response.getBody());
    	
    }
}