package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.RouteSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RouteScheduleRepository extends JpaRepository<RouteSchedule, UUID> {

    List<RouteSchedule> findByRouteId(UUID routeId);

    List<RouteSchedule> findByDayOfWeekAndIsActiveTrue(DayOfWeek dayOfWeek);

    @Query("SELECT rs FROM RouteSchedule rs JOIN Route r ON rs.routeId = r.routeId " +
            "WHERE r.status = 'ACTIVE' AND rs.isActive = true AND rs.dayOfWeek = :dayOfWeek")
    List<RouteSchedule> findActiveSchedulesByDay(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT rs FROM RouteSchedule rs WHERE rs.routeId = :routeId AND rs.dayOfWeek = :dayOfWeek AND rs.isActive = true")
    List<RouteSchedule> findActiveSchedulesByRouteAndDay(
            @Param("routeId") UUID routeId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

    @Query("SELECT DISTINCT rs FROM RouteSchedule rs JOIN RouteStop rst ON rs.routeId = rst.routeId " +
            "WHERE rst.hubId = :hubId AND rs.isActive = true")
    List<RouteSchedule> findActiveSchedulesByHub(@Param("hubId") UUID hubId);

    @Query("SELECT COUNT(rs) FROM RouteSchedule rs WHERE rs.routeId = :routeId AND rs.dayOfWeek = :dayOfWeek AND " +
            "(((:startTime >= rs.startTime AND :startTime < rs.endTime) OR " +
            "(:endTime > rs.startTime AND :endTime <= rs.endTime)) OR " +
            "(:startTime <= rs.startTime AND :endTime >= rs.endTime))")
    int findExistedRouteSchedule(UUID routeId, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek);


    List<RouteSchedule> findAllByIsActiveIsTrue();

    List<RouteSchedule> findAllByIsActiveIsTrueAndRouteId(UUID routeId);
}