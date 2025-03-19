package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Trip;

import java.util.UUID;

public interface TripService {
    // Create a new trip for today
    Trip createTripForToday(UUID routeVehicleId, String username);

    // Start a trip
    Trip startTrip(UUID tripId);

    // Advance to next stop
    Trip advanceToNextStop(UUID tripId);

    // Complete a trip
    Trip completeTrip(UUID tripId);
}
