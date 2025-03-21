package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.DriverTripsDTO;
import openerp.openerpresourceserver.dto.RouteStopDto;
import openerp.openerpresourceserver.dto.TripDetailsDTO;
import openerp.openerpresourceserver.dto.TripSummaryDTO;
import openerp.openerpresourceserver.entity.Trip;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TripService {

    DriverTripsDTO getAllTripsForDriver(String username);

    TripDetailsDTO getTripDetailsForDriver(UUID tripId, String username);

    @Transactional
    TripDetailsDTO startTrip(UUID tripId, String username);

    @Transactional
    TripDetailsDTO advanceToNextStop(UUID tripId, String username);

    @Transactional
    TripSummaryDTO completeTrip(UUID tripId, String username, String completionNotes);

    @Transactional
    Trip createTripForToday(UUID routeVehicleId, String username);

    Map<String, Object> getTripDetails(UUID tripId, String username);

    List<RouteStopDto> getTripStops(UUID tripId, String username);

    Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes);
}
