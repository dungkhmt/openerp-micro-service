package openerp.openerpresourceserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinateRequest;
import openerp.openerpresourceserver.dto.response.RoutePathResponse;
import openerp.openerpresourceserver.service.route.RouteService;

@RestController
@RequestMapping("/routes")
@AllArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @Secured("ROLE_WMS_DELIVERY_MANAGER")
    @PostMapping("/fetch")
    public ResponseEntity<RoutePathResponse> getRoute(@RequestBody CoordinateRequest request) {
        RoutePathResponse response = routeService.getRoute(request);
        return ResponseEntity.ok(response);
    }


}