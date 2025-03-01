package openerp.openerpresourceserver.service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteStop;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    @Transactional
    Route createRoute(Route route, List<RouteStop> stops);

    @Transactional
    Route updateRoute(UUID routeId, Route routeDetails, List<RouteStop> stops);

    Route getRouteById(UUID routeId);

    List<RouteStop> getRouteStops(UUID routeId);

    List<Route> getAllRoutes();

    List<Route> getRoutesByStatus(Route.RouteStatus status);

    List<Route> getRoutesByHub(UUID hubId);

    @Transactional
    void deleteRoute(UUID routeId);

    void updateRouteMetrics(UUID routeId);
}
