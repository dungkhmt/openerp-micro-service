package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.Trip;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TripService {

    List<TripDTO> getAllTripsForDriver(String username);

    List<TripDTO> getAllTripsForDriverToday(String username);

    TripDetailsDTO getTripDetailsForDriver(UUID tripId, String username);

    @Transactional
    TripDetailsDTO startTrip(UUID tripId, String username);

    @Transactional
    TripDetailsDTO advanceToNextStop(UUID tripId, String username);

    @Transactional
    TripSummaryDTO completeTrip(UUID tripId, String username, String completionNotes);

    @Transactional
    List<Trip> createTripThisWeek();

    @Transactional
    List<Trip> createTripFromTo(LocalDate startDate, LocalDate endDate);

    @Transactional
    Trip createTripForToday(UUID routeVehicleId, String username);

    Map<String, Object> getTripDetails(UUID tripId, String username);

    List<RouteStopDto> getTripStops(UUID tripId, String username);

    Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes);
}
