package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Route;
import openerp.openerpresourceserver.entity.RouteVehicle;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    List<RouteVehicle> getScheduleByDateRange(Instant startDate, Instant endDate);

    List<RouteVehicle> getVehicleWeeklySchedule(UUID vehicleId);

    List<RouteVehicle> getDriverWeeklySchedule(UUID driverId);

    List<Route> findSuitableRoutesForOrder(UUID originHubId, UUID destinationHubId);

    boolean isInDateRange(Instant date, Instant start, Instant end);
}
