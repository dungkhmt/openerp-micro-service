package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByRouteVehicleId(UUID routeVehicleId);
    List<Trip> findByDriverIdAndTripDate(UUID driverId, LocalDate tripDate);
    Trip findByRouteVehicleIdAndTripDateAndStatus(UUID routeVehicleId, LocalDate tripDate, TripStatus status);
}