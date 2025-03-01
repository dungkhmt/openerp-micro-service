package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.RouteVehicle;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RouteVehicleService {
    @Transactional
    RouteVehicle assignVehicleToRoute(UUID routeId, UUID vehicleId, String direction);

    @Transactional
    RouteVehicle updateRouteVehicle(UUID id, String direction);

    @Transactional
    void unassignVehicle(UUID routeVehicleId);

    List<RouteVehicle> getRouteVehiclesByRoute(UUID routeId);

    List<RouteVehicle> getRouteVehiclesByVehicle(UUID vehicleId);

    List<RouteVehicle> getRouteVehiclesByDriver(UUID driverId);

    List<RouteVehicle> getRouteVehiclesByDateRange(Instant startDate, Instant endDate);

    RouteVehicle getRouteVehicleById(UUID id);
}
