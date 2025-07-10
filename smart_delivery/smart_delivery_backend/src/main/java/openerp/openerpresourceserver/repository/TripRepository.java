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
    List<Trip> findByRouteScheduleId(UUID routeVehicleId);

    List<Trip> findActiveByDriverId(UUID id);

    @Query("SELECT t FROM Trip t WHERE t.routeScheduleId = :routeScheduleId AND t.startTime >= :startTime AND t.startTime < :endTime")
    List<Trip> findByRouteScheduleIdAndDateRange(
            @Param("routeScheduleId") UUID routeScheduleId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    Optional<Trip> findActiveByRouteScheduleId(UUID routeScheduleId);

    List<Trip> findByDriverId(UUID id);

    List<Trip> findByDriverIdAndDate(UUID id, LocalDate now);

    @Query("select distinct t from Trip t join RouteSchedule rs on t.routeScheduleId = rs.id join RouteStop rst on rs.routeId = rst.routeId where rst.hubId = :hubId and t.status = :status and t.date = :date")
    List<Trip> findAllTripsByHubIdAndStatusAndDate(@Param("hubId") UUID hubId, @Param("status") String tripStatus, @Param("date") LocalDate date);

    @Query("select distinct t from Trip t join RouteSchedule rs on t.routeScheduleId = rs.id join RouteStop rst on rs.routeId = rst.routeId where rst.hubId = :hubId and t.date = :date")
    List<Trip> findAllTripsByHubIdAndDate(@Param("hubId") UUID hubId, @Param("date") LocalDate date);

    @Query("select distinct t from Trip t join RouteSchedule rs on t.routeScheduleId = rs.id join RouteStop rst on rs.routeId = rst.routeId where rst.hubId = :hubId and t.date = :date and rst.stopSequence = 1")
    List<Trip> findAllTripsByHubIdAndDateStart(@Param("hubId") UUID hubId, @Param("date") LocalDate date);
    @Query("select distinct t from Trip t join RouteSchedule rs on t.routeScheduleId = rs.id join RouteStop rst on rs.routeId = rst.routeId where rst.hubId = :hubId and t.date = :date and rst.stopSequence != 1")
    List<Trip> findAllTripsByHubIdAndDateThrough(@Param("hubId") UUID hubId, @Param("date") LocalDate date);
}