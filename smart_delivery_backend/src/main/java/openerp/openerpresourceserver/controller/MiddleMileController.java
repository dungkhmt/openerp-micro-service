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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/middle-mile")
@RequiredArgsConstructor
public class MiddleMileController {

    private final RouteService routeService;
    private final RouteVehicleService routeVehicleService;
    private final MiddleMileOrderService orderService;
    private final ScheduleService scheduleService;
    private final MiddleMileOrderService middleMileOrderService;

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

    // ===== Route Vehicle Endpoints =====
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @PostMapping("/vehicle-assignments")
    public ResponseEntity<RouteVehicle> assignVehicleToRoute(@Valid @RequestBody Map<String, Object> request) {
        UUID routeId = UUID.fromString((String) request.get("routeId"));
        UUID vehicleId = UUID.fromString((String) request.get("vehicleId"));

        return ResponseEntity.ok(routeVehicleService.assignVehicleToRoute(routeId, vehicleId));
    }

    // ===== Route Vehicle Endpoints =====
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @GetMapping("/vehicle-assignments")
    public ResponseEntity<List<RouteVehicleDto>> getAllVehicleAssignments() {

        return ResponseEntity.ok(routeVehicleService.getAllVehicleAssignments());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @PutMapping("/vehicle-assignments/{id}")
    public ResponseEntity<RouteVehicle> updateRouteVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(routeVehicleService.updateRouteVehicle(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @DeleteMapping("/vehicle-assignments/{id}")
    public ResponseEntity<Void> unassignVehicle(@PathVariable UUID id) {
        routeVehicleService.unassignVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/routes/{routeId}/vehicles")
    public ResponseEntity<List<RouteVehicle>> getRouteVehiclesByRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeVehicleService.getRouteVehiclesByRoute(routeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'ROUTE_MANAGER')")
    @GetMapping("/vehicles/{vehicleId}/routes")
    public ResponseEntity<List<RouteVehicleDetailDto>> getRouteVehiclesByVehicle(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(routeVehicleService.getRouteVehiclesByVehicle(vehicleId));
    }

    @GetMapping("/drivers/{driverId}/routes")
    public ResponseEntity<List<RouteVehicle>> getRouteVehiclesByDriver(@PathVariable UUID driverId) {
        return ResponseEntity.ok(routeVehicleService.getRouteVehiclesByDriver(driverId));
    }

    @GetMapping("/vehicle-assignments/{id}")
    public ResponseEntity<RouteVehicle> getRouteVehicleById(@PathVariable UUID id) {
        return ResponseEntity.ok(routeVehicleService.getRouteVehicleById(id));
    }

    // ===== Order Endpoints =====
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @PostMapping("/trips/{routeVehicleId}/orders")
    public ResponseEntity<Void> assignOrdersToTrip(
            @PathVariable UUID routeVehicleId,
            @RequestBody List<UUID> orderIds) {
        orderService.assignOrdersToTrip(routeVehicleId, orderIds);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER')")
    @DeleteMapping("/orders/{orderId}/trip")
    public ResponseEntity<Void> unassignOrderFromTrip(@PathVariable UUID orderId) {
        orderService.unassignOrderFromTrip(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trips/{routeVehicleId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByTrip(@PathVariable UUID routeVehicleId) {
        return ResponseEntity.ok(orderService.getOrdersByTrip(routeVehicleId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'DRIVER')")
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody Map<String, String> request) {
        OrderStatus status = OrderStatus.valueOf(request.get("status"));
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'DRIVER')")
    @PostMapping("/trips/{routeVehicleId}/complete")
    public ResponseEntity<Void> completeTrip(@PathVariable UUID routeVehicleId) {
        orderService.completeTrip(routeVehicleId);
        return ResponseEntity.ok().build();
    }

    // ===== Schedule Endpoints =====
    @GetMapping("/schedule")
    public ResponseEntity<List<RouteVehicle>> getScheduleByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return ResponseEntity.ok(scheduleService.getScheduleByDateRange(startDate, endDate));
    }

    @GetMapping("/vehicles/{vehicleId}/schedule/week")
    public ResponseEntity<List<RouteVehicle>> getVehicleWeeklySchedule(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(scheduleService.getVehicleWeeklySchedule(vehicleId));
    }

    @GetMapping("/drivers/{driverId}/schedule/week")
    public ResponseEntity<List<RouteVehicle>> getDriverWeeklySchedule(@PathVariable UUID driverId) {
        return ResponseEntity.ok(scheduleService.getDriverWeeklySchedule(driverId));
    }

    @GetMapping("/routes/find")
    public ResponseEntity<List<Route>> findSuitableRoutesForOrder(
            @RequestParam UUID originHubId,
            @RequestParam UUID destinationHubId) {
        return ResponseEntity.ok(scheduleService.findSuitableRoutesForOrder(originHubId, destinationHubId));
    }

    @GetMapping("/orders/vehicle/{vehicleId}/{hubId}/{direction}")
    public ResponseEntity<List<OrderSummaryMiddleMileDto>> findSuitableOrdersForVehicle(
            @PathVariable UUID vehicleId,
            @PathVariable UUID hubId,
            @PathVariable RouteDirection direction
            ) {
        return ResponseEntity.ok(middleMileOrderService.getCollectedHubListVehicle(vehicleId, hubId));
    }



}