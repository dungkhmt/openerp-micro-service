package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Trip;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByRouteVehicleId(UUID routeVehicleId);

    Optional<Trip> findActiveByRouteVehicleId(UUID routeVehicleId);

    List<Trip> findActiveByDriverId(UUID id);

    @Query("SELECT t FROM Trip t WHERE t.routeVehicleId = :routeVehicleId AND t.startTime >= :startTime AND t.startTime < :endTime")
    List<Trip> findByRouteVehicleIdAndDateRange(
            @Param("routeVehicleId") UUID routeVehicleId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);
}