package openerp.openerpresourceserver.service.impl;
import jakarta.ws.rs.NotFoundException;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import openerp.openerpresourceserver.repository.DriverRepo;
import openerp.openerpresourceserver.repository.TripRepository;
import openerp.openerpresourceserver.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private DriverRepo driverRepo;

    // Create a new trip for today
    @Override
    public Trip createTripForToday(UUID routeVehicleId, String username) {
        // Check if there's already an active trip
        Driver driver = driverRepo.findByUsername(username);
        if(driver == null) throw new NotFoundException("Driver not found");
        Trip existingTrip = tripRepository.findByRouteVehicleIdAndTripDateAndStatus(
                routeVehicleId, LocalDate.now(), TripStatus.IN_PROGRESS);

        if (existingTrip != null) {
            return existingTrip;
        }

        // Create new trip
        Trip trip = new Trip();
        trip.setRouteVehicleId(routeVehicleId);
        trip.setDriverId(driver.getId());
        trip.setDriverName(driver.getName());
        trip.setTripDate(LocalDate.now());
        trip.setStatus(TripStatus.PENDING);
        trip.setCurrentStopSequence(1);

        return tripRepository.save(trip);
    }

    // Start a trip
    @Override
    public Trip startTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setDepartureTime(Timestamp.valueOf(LocalDateTime.now()));

        return tripRepository.save(trip);
    }

    // Advance to next stop
    @Override
    public Trip advanceToNextStop(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        trip.setCurrentStopSequence(trip.getCurrentStopSequence() + 1);
        trip.setCompletedStops(trip.getCompletedStops() + 1);

        return tripRepository.save(trip);
    }

    // Complete a trip
    @Override
    public Trip completeTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found"));

        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletionTime(Timestamp.valueOf(LocalDateTime.now()));

        return tripRepository.save(trip);
    }

}
