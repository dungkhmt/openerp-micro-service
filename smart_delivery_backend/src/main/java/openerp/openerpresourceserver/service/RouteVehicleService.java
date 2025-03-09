package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.RouteVehicleDetailDto;
import openerp.openerpresourceserver.dto.RouteVehicleDto;
import openerp.openerpresourceserver.entity.RouteVehicle;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RouteVehicleService {
    List<RouteVehicleDto> getAllVehicleAssignments();

    @Transactional
    RouteVehicle assignVehicleToRoute(UUID routeId, UUID vehicleId, String direction);

    @Transactional
    RouteVehicle updateRouteVehicle(UUID id, String direction);

    @Transactional
    void unassignVehicle(UUID routeVehicleId);

    List<RouteVehicle> getRouteVehiclesByRoute(UUID routeId);

    List<RouteVehicleDetailDto> getRouteVehiclesByVehicle(UUID vehicleId);

    List<RouteVehicle> getRouteVehiclesByDriver(UUID driverId);

    List<RouteVehicle> getRouteVehiclesByDateRange(Instant startDate, Instant endDate);

    RouteVehicle getRouteVehicleById(UUID id);

}
