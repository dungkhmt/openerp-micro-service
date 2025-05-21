package openerp.openerpresourceserver.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.RouteDirection;
import openerp.openerpresourceserver.service.*;
import openerp.openerpresourceserver.service.impl.MiddleMileOrderServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/middle-mile")
@RequiredArgsConstructor
public class MiddleMileController {

    private final RouteService routeService;
    private final MiddleMileOrderService orderService;
    private final MiddleMileOrderService middleMileOrderService;
    private final AssignmentService assignmentService;
    private final DriverService driverService;

    @PreAuthorize("hasAnyRole('ADMIN','HUB_MANAGER')")
    @PostMapping("/assign-orders")
    public void assign(){
        assignmentService.assignOrderItemsForTripsForAllHubs();
    }

    // Get suggested orders for trip
    @GetMapping("/trip/{tripId}/suggested-orders")
    public ResponseEntity<List<OrderSuggestionDto>> getSuggestedOrders(@PathVariable UUID tripId) {
        List<OrderSuggestionDto> suggestions = driverService.getSuggestedOrderItemsForTrip(tripId);
        return ResponseEntity.ok(suggestions);
    }

    // Hub staff assigns selected orders to trip
    @PostMapping("/trip/{tripId}/assign-orders")
    public ResponseEntity<Void> assignOrdersToTrip(Principal principal,
            @PathVariable UUID tripId,
            @RequestBody AssignOrdersRequest request) {

        middleMileOrderService.assignAndConfirmOrdersOut( principal, tripId, request.getOrderIds());
        return ResponseEntity.ok().build();
    }
    // ===== Route Endpoints =====
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @PostMapping("/routes")
    public ResponseEntity<RouteDto> createRoute(@Valid @RequestBody RouteDto routeDto) {

        return ResponseEntity.ok(routeService.createRoute(routeDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @PutMapping("/routes/{routeId}")
    public ResponseEntity<Route> updateRoute(
            @PathVariable UUID routeId,
            @Valid @RequestBody Map<String, Object> request) {
        Route route = new Route();
        route.setRouteCode((String) request.get("routeCode"));
        route.setRouteName((String) request.get("routeName"));
        route.setDescription((String) request.get("description"));
        route.setNotes((String) request.get("notes"));
        route.setStatus(Route.RouteStatus.valueOf((String) request.get("status")));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stopsData = (List<Map<String, Object>>) request.get("stops");
        List<RouteStop> stops = stopsData.stream()
                .map(stopData -> {
                    RouteStop stop = new RouteStop();
                    stop.setHubId(UUID.fromString((String) stopData.get("hubId")));
                    stop.setStopSequence((Integer) stopData.get("stopSequence"));
                    stop.setEstimatedWaitTime((Integer) stopData.get("estimatedWaitTime"));
                    return stop;
                })
                .toList();

        return ResponseEntity.ok(routeService.updateRoute(routeId, route, stops));
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<Route> getRouteById(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeService.getRouteById(routeId));
    }

    @GetMapping("/routes/{routeId}/stops")
    public ResponseEntity<List<RouteStopDto>> getRouteStops(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeService.getRouteStops(routeId));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/routes/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(@PathVariable Route.RouteStatus status) {
        return ResponseEntity.ok(routeService.getRoutesByStatus(status));
    }

    @GetMapping("/routes/hub/{hubId}")
    public ResponseEntity<List<Route>> getRoutesByHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(routeService.getRoutesByHub(hubId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @DeleteMapping("/routes/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable UUID routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @GetMapping("/routes/{routeId}/vehicles")
    public ResponseEntity<List<VehicleDto>> getVehicleForRoute(@PathVariable UUID routeId) {

        return ResponseEntity.ok(routeService.getVehicleForRoute(routeId));
    }
}