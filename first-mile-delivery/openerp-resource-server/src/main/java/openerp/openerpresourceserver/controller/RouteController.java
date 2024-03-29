package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.RouteDTO;
import openerp.openerpresourceserver.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/route")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class RouteController {

    private RouteService routeService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoute(@RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok().body(routeService.createRoute(routeDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID id) {
        return ResponseEntity.ok().body(routeService.deleteRoute(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable UUID id, @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok().body(routeService.updateRoute(id, routeDTO));
    }

    @PostMapping("/add-order")
    public ResponseEntity<?> addOrderToRoute(@RequestBody Map<String, UUID> request) {
        UUID orderId = request.get("orderId");
        UUID routeId = request.get("routeId");
        routeService.addNewOrderToRoute(orderId, routeId);
        return ResponseEntity.ok().body("Order added to route successfully");
    }

    @DeleteMapping("/remove-order")
    public ResponseEntity<?> removeOrderFromRoute(@RequestBody Map<String, UUID> request) {
        UUID orderId = request.get("orderId");
        UUID routeId = request.get("routeId");
        routeService.removeOrderFromRoute(orderId, routeId);
        return ResponseEntity.ok().body("Order removed from route successfully");
    }

    @PostMapping("move-order")
    public ResponseEntity<?> moveOrderToRoute(@RequestBody Map<String, UUID> request) {
        UUID orderId = request.get("orderId");
        UUID oldRouteId = request.get("oldRouteId");
        UUID newRouteId = request.get("newRouteId");
        routeService.removeOrderFromRoute(orderId, oldRouteId);
        routeService.addNewOrderToRoute(orderId, newRouteId);
        return ResponseEntity.ok().body("Order moved to new route successfully");
    }

    @PostMapping("/get-all-orders/{routeId}")
    public ResponseEntity<?> getAllOrdersInRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok().body(routeService.getAllOrdersInRoute(routeId));
    }

}
