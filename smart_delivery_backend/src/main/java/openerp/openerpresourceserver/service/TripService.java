package openerp.openerpresourceserver.service;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.dto.*;
import openerp.openerpresourceserver.entity.RouteStop;
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

    // Add this new method to retrieve trip history for frontend visualization
    List<TripHistoryDetailDto> getTripHistoryDetails(UUID tripId);

    // Add this new method to get a timeline-friendly representation of trip status changes
    TripTimelineDto getTripTimeline(UUID tripId);

    @Transactional
    TripDetailsDTO startTrip(UUID tripId, String username);

    @Transactional
    TripDetailsDTO arrivedInNextStop(UUID tripId, UUID hubId, String username);

    @Transactional
    TripDetailsDTO doneStop(UUID tripId, String username);

    @Transactional
    TripSummaryDTO completeTrip(UUID tripId, String username, String completionNotes);

    @Transactional
    List<Trip> createTripThisWeek();

    @Transactional
    List<Trip> createTripFromTo(LocalDate startDate, LocalDate endDate);

    @Transactional
    Trip createTripForToday(UUID routeVehicleId, String username);

    RouteStop getCurrentRouteStop(UUID tripId);

    Map<String, Object> getTripDetails(UUID tripId, String username);

    List<RouteStopDto> getTripStops(UUID tripId, String username);

    Map<String, Object> completeTripWithSummary(UUID tripId, String username, String notes);

    List<TripDTO> getTripsForHubToday(UUID hubId);

    List<TripDTO> getTripsForHubTodayStart(UUID hubId);

    List<TripDTO> getTripsForHubTodayThrough(UUID hubId);

    TripHistoryResponseDto getTripHistoryWithDetails(UUID tripId);

    List<TripDTO> getAllTrips();

    TripDetailsDTO getTripById(UUID id);

    void deleteTrip(UUID id);

    Trip updateTrip(UUID id, TripUpdateDto tripUpdateDTO, String changedBy);
}
