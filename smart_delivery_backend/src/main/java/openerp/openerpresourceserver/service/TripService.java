package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.RouteStopDto;
import openerp.openerpresourceserver.entity.Trip;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TripService {
    // Create a new trip for today
    Trip createTripForToday(UUID routeVehicleId, String username);

    Trip getActiveTripForRouteVehicle(UUID routeVehicleId, String username);


    Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes);

    Map<String, Object> getTripDetails(UUID tripId, String username);


    @Transactional
    Trip advanceToNextStop(UUID tripId, String username);

    List<RouteStopDto> getTripStops(UUID tripId, String username);

    List<Trip> getActiveTripsForDriver(String username);
}
